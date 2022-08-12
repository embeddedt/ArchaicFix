package org.embeddedt.archaicfix.threadedupdates;

import net.minecraft.client.shader.TesselatorVertexState;

public interface ICapturableTessellator {

    /** Like getVertexState, but doesn't sort the quads. */
    public TesselatorVertexState arch$getUnsortedVertexState();

    /** Add the quads inside a TessellatorVertexState to this tessellator. Does nothing and returns false if the
     *  supplied state is not compatible. */
    public boolean arch$addTessellatorVertexState(TesselatorVertexState state);

}
