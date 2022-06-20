package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCache;
import org.embeddedt.archaicfix.mixins.IWorldRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/* MC-129 */
@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IWorldRenderer {
    @Shadow private boolean isInitialized;
    @Shadow public boolean needsUpdate;

    @Shadow public abstract float distanceToEntitySquared(Entity p_78912_1_);

    @Shadow public boolean[] skipRenderPass;

    @Shadow private int glRenderList;

    @Shadow public int posXPlus;

    @Shadow public int posYPlus;

    @Shadow public int posZPlus;

    @Shadow public boolean isInFrustum;

    public boolean arch$isInView() {
        if(Minecraft.getMinecraft().renderViewEntity == null)
            return true;
        float distance = this.distanceToEntitySquared(Minecraft.getMinecraft().renderViewEntity);
        int renderDistanceBlocks = (Minecraft.getMinecraft().gameSettings.renderDistanceChunks) * 16;
        return distance <= (renderDistanceBlocks * renderDistanceBlocks);
    }

    /**
     * Make sure chunks re-render immediately (MC-129).
     */
    @Inject(method = "markDirty", at = @At("TAIL"))
    private void forceRender(CallbackInfo ci) {
        for(int i = 0; i < this.skipRenderPass.length; i++) {
            this.skipRenderPass[i] = false;
        }
    }

    /**
     * When switching worlds/dimensions, clear out the old render lists for old chunks. This prevents old dimension
     * content from being visible in the new world.
     */
    @Inject(method = "setDontDraw", at = @At("TAIL"))
    private void clearOldRenderList(CallbackInfo ci) {
        for(int pass = 0; pass < 2; pass++) {
            GL11.glNewList(this.glRenderList + pass, GL11.GL_COMPILE);
            GL11.glEndList();
        }
    }

    @Inject(method = "updateInFrustum", at = @At("HEAD"), cancellable = true)
    private void cullInCircularRadius(ICamera camera, CallbackInfo ci) {
        int renderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16;
        Entity renderViewEntity = Minecraft.getMinecraft().renderViewEntity;
        if(renderViewEntity != null) {
            double distance = this.distanceToEntitySquared(renderViewEntity);
            if(distance > (renderDistance*renderDistance)) {
                this.isInFrustum = false;
                ci.cancel();
            }
        }
    }
}
