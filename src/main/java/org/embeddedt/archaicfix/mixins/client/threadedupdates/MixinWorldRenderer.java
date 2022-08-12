package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.embeddedt.archaicfix.helpers.ThreadedChunkUpdateHelper;
import org.embeddedt.archaicfix.threadedupdates.ICapturableTessellator;
import org.embeddedt.archaicfix.threadedupdates.IRendererUpdateResultHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer implements IRendererUpdateResultHolder {

    private ThreadedChunkUpdateHelper.UpdateTask.Result arch$updateTaskResult;

    @Inject(method = "updateRenderer", at = @At("HEAD"))
    private void setLastUpdateResultSingleton(CallbackInfo ci) {
        ThreadedChunkUpdateHelper.lastUpdateResult = ((IRendererUpdateResultHolder)(Object)this).arch$getRendererUpdateResult();
    }

    @Inject(method = "postRenderBlocks", at = @At("HEAD"))
    private void loadTessellationResult(int pass, EntityLivingBase view, CallbackInfo ci) {
        if(pass == 0) {
            ((ICapturableTessellator) Tessellator.instance).arch$addTessellatorVertexState(arch$updateTaskResult.renderedQuads);
        }
    }

    @Override
    public ThreadedChunkUpdateHelper.UpdateTask.Result arch$getRendererUpdateResult() {
        return arch$updateTaskResult;
    }

    @Override
    public void arch$setRendererUpdateResult(ThreadedChunkUpdateHelper.UpdateTask.Result result) {
        arch$updateTaskResult = result;
    }
}
