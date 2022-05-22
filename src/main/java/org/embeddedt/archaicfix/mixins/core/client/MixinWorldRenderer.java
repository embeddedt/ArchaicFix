package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* MC-129 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
    @Shadow private boolean isInitialized;
    @Shadow public boolean needsUpdate;

    @Shadow public abstract float distanceToEntitySquared(Entity p_78912_1_);

    @Shadow public boolean[] skipRenderPass;

    @Inject(method = "markDirty", at = @At("TAIL"))
    private void forceRender(CallbackInfo ci) {
        for(int i = 0; i < this.skipRenderPass.length; i++) {
            this.skipRenderPass[i] = false;
        }
    }

    @Redirect(method = "updateRenderer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ChunkCache;extendedLevelsInChunkCache()Z"))
    private boolean checkEmpty(ChunkCache cache) {
        if(cache.extendedLevelsInChunkCache())
            return true;
        if(Minecraft.getMinecraft().renderViewEntity == null)
            return false;
        float distance = this.distanceToEntitySquared(Minecraft.getMinecraft().renderViewEntity);
        int renderDistanceBlocks = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16;
        return distance > (renderDistanceBlocks * renderDistanceBlocks);
    }
}
