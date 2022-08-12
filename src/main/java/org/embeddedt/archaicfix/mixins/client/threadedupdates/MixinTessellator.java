package org.embeddedt.archaicfix.mixins.client.threadedupdates;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import org.embeddedt.archaicfix.threadedupdates.ICapturableTessellator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Tessellator.class)
public class MixinTessellator implements ICapturableTessellator {

    @Shadow
    private int[] rawBuffer;

    @Shadow
    private int rawBufferIndex;

    @Shadow
    private int vertexCount;

    @Override
    public TesselatorVertexState arch$getUnsortedVertexState() {
        // TODO
        return ((Tessellator)(Object)this).getVertexState(0, 0, 0);
    }

    @Override
    public boolean arch$addTessellatorVertexState(TesselatorVertexState state) {
        if(state == null) return true;

        // TODO check if draw mode and flags are the same
        System.arraycopy(state.getRawBuffer(), 0, rawBuffer, rawBufferIndex, state.getRawBuffer().length);
        rawBufferIndex += state.getRawBufferIndex();
        vertexCount += state.getVertexCount();

        return true;
    }
}
