package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import org.embeddedt.archaicfix.threadedupdates.ICapturableTessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(Tessellator.class)
public abstract class MixinTessellator implements ICapturableTessellator {

    @Shadow
    private int[] rawBuffer;

    @Shadow
    private int rawBufferIndex;

    @Shadow
    private int vertexCount;

    @Shadow protected abstract void reset();

    @Shadow private boolean isDrawing;

    @Shadow private int rawBufferSize;

    @Override
    public TesselatorVertexState arch$getUnsortedVertexState() {
        if(vertexCount < 1) {
            return null;
        }
        // TODO
        return ((Tessellator)(Object)this).getVertexState(0, 0, 0);
    }

    @Override
    public boolean arch$addTessellatorVertexState(TesselatorVertexState state) {
        if(state == null) return true;
        // TODO check if draw mode and flags are the same

        while(rawBufferSize < rawBufferIndex + state.getRawBuffer().length) {
            rawBufferSize *= 2;
        }
        if(rawBufferSize > rawBuffer.length) {
            rawBuffer = Arrays.copyOf(rawBuffer, rawBufferSize);
        }

        System.arraycopy(state.getRawBuffer(), 0, rawBuffer, rawBufferIndex, state.getRawBuffer().length);
        rawBufferIndex += state.getRawBufferIndex();
        vertexCount += state.getVertexCount();

        return true;
    }

    @Override
    public void discard() {
        isDrawing = false;
        reset();
    }
}
