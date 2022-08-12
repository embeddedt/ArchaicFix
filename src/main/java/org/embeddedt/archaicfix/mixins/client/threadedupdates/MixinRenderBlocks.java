package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.ForgeHooksClient;
import org.embeddedt.archaicfix.helpers.ThreadedChunkUpdateHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {

    private Tessellator arch$savedTessellatorInstance;

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void cancelRenderDelegatedToDifferentThread(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        arch$savedTessellatorInstance = Tessellator.instance;

        boolean offThreadBlock = ThreadedChunkUpdateHelper.canBlockBeRenderedOffThread(block, ForgeHooksClient.getWorldRenderPass());
        if(Thread.currentThread() == ThreadedChunkUpdateHelper.MAIN_THREAD ? offThreadBlock : !offThreadBlock) {
            // Cancel rendering block if it's delegated to a different thread.
            cir.setReturnValue(ThreadedChunkUpdateHelper.lastUpdateResult.renderedSomething);
        } else {
            if(Thread.currentThread() != ThreadedChunkUpdateHelper.MAIN_THREAD) {
                // This is dangerous, it might be better to change it to a wildcard field access redirect.
                Tessellator.instance = ThreadedChunkUpdateHelper.instance.threadTessellator;
            }
        }
    }

    @Inject(method = "renderBlockByRenderType", at = @At("RETURN"))
    private void postRenderBlockByRenderType(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        Tessellator.instance = arch$savedTessellatorInstance;
    }

}
