package org.embeddedt.archaicfix.mixins.common.gt6;

import gregapi.util.CR;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.ducks.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CR.class)
public class MixinCR {
    @Redirect(method = "remove([Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/crafting/IRecipe;matches(Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/world/World;)Z", remap = true), remap = false)
    private static boolean fasterMatchCheck(IRecipe recipe, InventoryCrafting crafting, World world, ItemStack[] inputStacks) {
        if(recipe instanceof IAcceleratedRecipe) {
            IAcceleratedRecipe accel = (IAcceleratedRecipe)recipe;
            if(accel.getPotentialItems() != null) {
                for(ItemStack stack : inputStacks) {
                    if(stack != null && !accel.getPotentialItems().contains(stack.getItem())) {
                        return false;
                    }
                }
            }
        }
        return recipe.matches(crafting, world);
    }
}
