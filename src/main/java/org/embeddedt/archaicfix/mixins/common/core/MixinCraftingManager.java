package org.embeddedt.archaicfix.mixins.common.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.UncheckedExecutionException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.recipe.IFasterCraftingManager;
import org.embeddedt.archaicfix.recipe.LastMatchedInfo;
import org.embeddedt.archaicfix.recipe.RecipeCacheLoader;
import org.embeddedt.archaicfix.recipe.RecipeWeigher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Mixin(CraftingManager.class)
public class MixinCraftingManager implements IFasterCraftingManager {
    private final LoadingCache<Set<Item>, IRecipe[]> potentialRecipes = CacheBuilder.newBuilder()
            /* 500k IRecipe references times 8 bytes per reference = 4 million bytes */
            .maximumWeight(500000)
            .weigher(new RecipeWeigher())
            .build(new RecipeCacheLoader());

    private volatile LastMatchedInfo lastMatchedInfo = null;

    @Inject(method = "findMatchingRecipe", at = @At(value = "CONSTANT", args = "intValue=0"), slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;II)V"),
            to =  @At(value = "INVOKE", target = "Ljava/util/List;size()I")
    ), cancellable = true)
    private void fasterRecipeSearch(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir) {
        LastMatchedInfo retInfo = lastMatchedInfo;
        if(retInfo == null || !retInfo.matches(inventory)) {
            Set<Item> stacks = new HashSet<>();
            for(int x = 0; x < inventory.getSizeInventory(); x++) {
                ItemStack stack = inventory.getStackInSlot(x);
                if(stack != null)
                    stacks.add(stack.getItem());
            }
            stacks = ImmutableSet.copyOf(stacks);
            IRecipe result = null;
            IRecipe[] recipes;
            try {
                recipes = potentialRecipes.get(stacks);
            } catch (UncheckedExecutionException | ExecutionException e) {
                ArchaicLogger.LOGGER.error("An error occured while attempting to cache recipes!", e);
                return;
            }
            for(IRecipe r : recipes) {
                if(r.matches(inventory, world)) {
                    result = r;
                    break;
                }
            }
            retInfo = new LastMatchedInfo(result, inventory);
            lastMatchedInfo = retInfo;
        }
        cir.setReturnValue(retInfo.getCraftingResult(inventory));
    }

    @Override
    public void clearRecipeCache() {
        potentialRecipes.invalidateAll();
        lastMatchedInfo = null;
    }
}
