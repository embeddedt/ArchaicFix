package org.embeddedt.archaicfix.mixins.common.tc4tweaks;

import org.embeddedt.archaicfix.thaumcraft.MappingsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.client.gui.MappingThread;

@Mixin(MappingThread.class)
public class MixinMappingThread {
    @Inject(method = "run", at = @At("HEAD"), remap = false, cancellable = true)
    private void fasterMappingComputation(CallbackInfo ci) {
        ci.cancel();
        MappingsHandler.handle();
    }
}
