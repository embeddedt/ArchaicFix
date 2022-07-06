package org.embeddedt.archaicfix.occlusion;

public interface IWorldRenderer {

    /** Returns the number of the last frame when this renderer was visited by the occlusion culling algorithm. */
    public int getLastCullUpdateFrame();

    /** Sets the number of the last frame when this renderer was visited by the occlusion culling algorithm. */
    public void setLastCullUpdateFrame(int lastCullUpdateFrame);

}
