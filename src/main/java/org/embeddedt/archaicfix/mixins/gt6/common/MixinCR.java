package org.embeddedt.archaicfix.mixins.gt6.common;

import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregapi.code.ItemStackContainer;
import gregapi.data.CS;
import gregapi.data.MT;
import gregapi.data.OP;
import gregapi.recipes.ICraftingRecipeGT;
import gregapi.util.CR;
import gregtech.GT6_Main;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.mixins.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Mixin(GT6_Main.class)
public class MixinCR {
    @Inject(method = "onModServerStarting2", at = @At(value = "INVOKE", target = "Lgregapi/util/CR;remove([Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", remap = false), remap = false, cancellable = true)
    private void getSmallerList(FMLServerStartingEvent event, CallbackInfo ci) {
        ci.cancel();
        List<IRecipe> recipeList = CR.list();
        ItemStack pyroStack = OP.dust.mat(MT.Pyrotheum, 1);
        Item tPyrotheum = pyroStack.getItem();
        Set<Item> oreItems = OP.ore.mRegisteredItems.stream().map(container -> container.mItem).collect(Collectors.toSet());
        int[] recipeIdxs = IntStream.range(0, recipeList.size()).parallel().filter(idx -> {
            IRecipe recipe = recipeList.get(idx);
            if(recipe instanceof IAcceleratedRecipe) {
                IAcceleratedRecipe accel = (IAcceleratedRecipe)recipe;
                if(accel.getPotentialItems() != null) {
                    return accel.getPotentialItems().contains(tPyrotheum) && !Collections.disjoint(oreItems, accel.getPotentialItems());
                }
            }
            return true;
        }).toArray();
        ArchaicLogger.LOGGER.info("Found " + recipeIdxs.length + " TE recipes to consider removing");
        for(int i = recipeIdxs.length - 1; i >= 0; i--) {
            IRecipe recipe = recipeList.get(recipeIdxs[i]);
            if((!(recipe instanceof ICraftingRecipeGT) || ((ICraftingRecipeGT)recipe).isRemovableByGT())) {
                for(ItemStackContainer tStack : OP.ore.mRegisteredItems) {
                    if(recipe instanceof IAcceleratedRecipe && !((IAcceleratedRecipe) recipe).getPotentialItems().contains(tStack.mItem))
                        continue;
                    InventoryCrafting aCrafting = CR.crafting(tStack.toStack(), pyroStack);
                    if(recipe.matches(aCrafting, CS.DW)) {
                        recipeList.remove(recipeIdxs[i]);
                        break;
                    }
                }
            }
        }
        ArchaicLogger.LOGGER.info("Finished removing TE recipes");
    }
}
