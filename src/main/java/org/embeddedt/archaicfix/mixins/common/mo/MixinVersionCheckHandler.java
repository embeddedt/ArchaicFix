package org.embeddedt.archaicfix.mixins.common.mo;

import cpw.mods.fml.common.gameevent.TickEvent;
import matteroverdrive.handler.VersionCheckerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VersionCheckerHandler.class)
public class MixinVersionCheckHandler {
    @Inject(method = "onPlayerTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void skipUpdate(TickEvent.PlayerTickEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
