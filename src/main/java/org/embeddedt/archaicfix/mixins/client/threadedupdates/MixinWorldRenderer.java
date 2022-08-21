package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.embeddedt.archaicfix.threadedupdates.ThreadedChunkUpdateHelper;
import org.embeddedt.archaicfix.threadedupdates.ICapturableTessellator;
import org.embeddedt.archaicfix.threadedupdates.IRendererUpdateResultHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IRendererUpdateResultHolder {

    private ThreadedChunkUpdateHelper.UpdateTask arch$updateTask;

    @Inject(method = "updateRenderer", at = @At("HEAD"))
    private void setLastWorldRendererSingleton(CallbackInfo ci) {
        ThreadedChunkUpdateHelper.lastWorldRenderer = ((WorldRenderer)(Object)this);
    }

    @Inject(method = "postRenderBlocks", at = @At("HEAD"))
    private void loadTessellationResult(int pass, EntityLivingBase view, CallbackInfo ci) {
        if(!arch$getRendererUpdateTask().cancelled) {
            ((ICapturableTessellator) Tessellator.instance).arch$addTessellatorVertexState(arch$getRendererUpdateTask().result[pass].renderedQuads);
        }
    }

    @Override
    public ThreadedChunkUpdateHelper.UpdateTask arch$getRendererUpdateTask() {
        if(arch$updateTask == null) {
            arch$updateTask = new ThreadedChunkUpdateHelper.UpdateTask();
        }
        return arch$updateTask;
    }

    @Inject(method = "markDirty", at = @At("RETURN"))
    private void notifyDirty(CallbackInfo ci) {
        ThreadedChunkUpdateHelper.instance.onWorldRendererDirty((WorldRenderer)(Object)this);
    }

}
