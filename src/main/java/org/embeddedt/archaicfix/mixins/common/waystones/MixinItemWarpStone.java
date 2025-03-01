package org.embeddedt.archaicfix.mixins.common.waystones;

import cpw.mods.fml.common.FMLCommonHandler;

import net.blay09.mods.waystones.item.ItemWarpStone;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemWarpStone.class)
public class MixinItemWarpStone {
    @Inject(method = "getDisplayDamage", at = @At("HEAD"), cancellable = true, remap = false)
    public void getDisplayDamage(ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            cir.setReturnValue(0);
            cir.cancel();
        }
    }
}
