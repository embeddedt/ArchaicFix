package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import lombok.SneakyThrows;
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

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void init(CallbackInfo ci) {
        arch$updateTask = new ThreadedChunkUpdateHelper.UpdateTask();
    }

    @Inject(method = "updateRenderer", at = @At("HEAD"))
    private void setLastWorldRendererSingleton(CallbackInfo ci) {
        ThreadedChunkUpdateHelper.lastWorldRenderer = ((WorldRenderer)(Object)this);
    }

    @SneakyThrows
    @Inject(method = "postRenderBlocks", at = @At("HEAD"))
    private void loadTessellationResult(int pass, EntityLivingBase view, CallbackInfo ci) {
        ((ICapturableTessellator) Tessellator.instance).arch$addTessellatorVertexState(arch$updateTask.result[pass].renderedQuads);
    }

    @Override
    public ThreadedChunkUpdateHelper.UpdateTask arch$getRendererUpdateTask() {
        return arch$updateTask;
    }

}
