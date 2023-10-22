package org.embeddedt.archaicfix.mixins.client.core;

import net.minecraft.client.renderer.OpenGlHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGlHelper.class)
public class MixinOpenGlHelper {
    @Inject(method = "setActiveTexture", at = @At("RETURN"))
    private static void alsoSetClientTexture(int texture, CallbackInfo ci) {
        OpenGlHelper.setClientActiveTexture(texture);
    }
}
