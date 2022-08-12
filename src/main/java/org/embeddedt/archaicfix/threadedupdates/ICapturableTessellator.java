package org.embeddedt.archaicfix.threadedupdates;

import net.minecraft.client.shader.TesselatorVertexState;

public interface ICapturableTessellator {

    /** Like getVertexState, but doesn't sort the quads. */
    public TesselatorVertexState arch$getUnsortedVertexState();

    /** Adds the quads inside a TessellatorVertexState to this tessellator. */
    public void arch$addTessellatorVertexState(TesselatorVertexState state);

    /** Flushes the tessellator's state similarly to draw(), but without drawing anything. */
    public void discard();

}
