package org.embeddedt.archaicfix.mixins.mo.common;

import matteroverdrive.handler.MatterRegistry;
import org.embeddedt.archaicfix.ArchaicFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;

@Mixin(MatterRegistry.class)
public class MixinMatterRegistry {
    @Inject(method = "needsCalculation", at = @At("HEAD"), cancellable = true, remap = false)
    private void skipCalcIfExists(String path, CallbackInfoReturnable<Boolean> cir) {
        if(new File(path).exists()) {
            ArchaicFix.LOGGER.warn("Skipping registry recalculation for Matter Overdrive as the registry file exists");
            cir.setReturnValue(false);
        }
    }
}
