package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.renderer.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* MC-129 */
@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow private boolean isInitialized;
    @Shadow public boolean needsUpdate;

    @Inject(method = "skipAllRenderPasses", at = @At("RETURN"), cancellable = true)
    public void dontSkipRenderIfNeedsUpdate(CallbackInfoReturnable<Boolean> cir) {
        if(this.isInitialized && this.needsUpdate) {
            cir.setReturnValue(false);
        }
    }
}
