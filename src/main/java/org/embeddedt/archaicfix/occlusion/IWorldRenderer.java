package org.embeddedt.archaicfix.occlusion;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.EnumFacing;

public interface IWorldRenderer {

    boolean arch$isInUpdateList();
    void arch$setInUpdateList(boolean b);

    boolean arch$isFrustumCheckPending();
    void arch$setIsFrustumCheckPending(boolean b);

    OcclusionHelpers.RenderWorker.CullInfo arch$getCullInfo();

}
