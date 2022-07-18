package org.embeddedt.archaicfix.occlusion;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.EnumFacing;

public interface IWorldRenderer {

    /** Sets the number of the last frame when this renderer was visited by the occlusion culling algorithm.
     *  Returns true if the value was changed as a result. */
    public boolean arch$setLastCullUpdateFrame(int lastCullUpdateFrame);

    boolean arch$isInUpdateList();
    void arch$setInUpdateList(boolean b);

    boolean arch$isFrustumCheckPending();
    void arch$setIsFrustumCheckPending(boolean b);

    public WorldRenderer arch$getNeighbor(EnumFacing dir);
    public void arch$setNeighbor(EnumFacing dir, WorldRenderer neighbor);

}
