package org.embeddedt.archaicfix.occlusion;

public interface IWorldRenderer {

    /** Sets the number of the last frame when this renderer was visited by the occlusion culling algorithm.
     *  Returns true if the value was changed as a result. */
    public boolean arch$setLastCullUpdateFrame(int lastCullUpdateFrame);

    boolean arch$isInUpdateList();
    void arch$setInUpdateList(boolean b);

}
