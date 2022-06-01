package org.embeddedt.archaicfix.mixins.core.common;

import codechicken.nei.ItemList;
import codechicken.nei.api.ItemInfo;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(CraftingManager.class)
public class MixinCraftingManager {
    @Shadow private List recipes;
    private IRecipe lastMatchedRecipe = null;
    private Integer lastMatchedHash = null;
    private ItemStack[] lastMatchedInventory = null;

    @Inject(method = "findMatchingRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), cancellable = true)
    private void fasterRecipeSearch(InventoryCrafting inventory, World world, CallbackInfoReturnable<ItemStack> cir) {
        int inventoryHash = arch$getHash(inventory);
        if(lastMatchedHash != null && inventoryHash == lastMatchedHash && matchesSavedInventory(inventory) && (lastMatchedRecipe == null || lastMatchedRecipe.matches(inventory, world))) {
            /* matched, do nothing */
        } else {
            lastMatchedRecipe = null;
            for (Object o : this.recipes) {
                IRecipe recipe = (IRecipe) o;
                if (recipe.matches(inventory, world)) {
                    lastMatchedRecipe = recipe;
                    break;
                }
            }
            lastMatchedHash = inventoryHash;
            lastMatchedInventory = new ItemStack[inventory.getSizeInventory()];
            for(int i = 0; i < lastMatchedInventory.length; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                lastMatchedInventory[i] = stack != null ? stack.copy() : null;
            }
        }
        cir.setReturnValue(lastMatchedRecipe != null ? lastMatchedRecipe.getCraftingResult(inventory) : null);
    }

    private boolean matchesSavedInventory(InventoryCrafting inventory) {
        if(lastMatchedInventory == null)
            return false;
        if(lastMatchedInventory.length != inventory.getSizeInventory())
            return false;
        for(int i = 0; i < lastMatchedInventory.length; i++) {
            ItemStack newStack = inventory.getStackInSlot(i);
            /* they definitely match */
            if(lastMatchedInventory[i] == null && newStack == null)
                continue;
            /* they don't match */
            if(lastMatchedInventory[i] == null || newStack == null)
                return false;
            /* now we know they are both non-null */
            if(!lastMatchedInventory[i].isItemEqual(newStack) || !ItemStack.areItemStackTagsEqual(lastMatchedInventory[i], newStack))
                return false;
        }
        return true;
    }

    private int arch$getHash(InventoryCrafting inventory) {
        int result = 1;
        for(int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            int hashCode = 1;
            if(stack != null) {
                //hashCode = 31 * hashCode + stack.stackSize;
                hashCode = 31 * hashCode + Item.getIdFromItem(stack.getItem());
                hashCode = 31 * hashCode + stack.getItemDamage();
                hashCode = 31 * hashCode + (!stack.hasTagCompound() ? 0 : stack.getTagCompound().hashCode());
            }
            result = 17 * result + hashCode;
        }
        return result;
    }
}
