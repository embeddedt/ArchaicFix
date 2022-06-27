package org.embeddedt.archaicfix.mixins.common.gt6;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import gregtech.GT6_Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GT6_Main.class)
public class MixinGT6_Main {
    @Inject(method = "onModServerStarting2", at = @At(value = "INVOKE", target = "Lgregapi/util/CR;remove([Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", remap = false), remap = false, cancellable = true)
    private void getSmallerList(FMLServerStartingEvent event, CallbackInfo ci) {
        ci.cancel();
        /* We made sure TE didn't register pyrotheum recipes already */
    }
}
