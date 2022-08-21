package org.embeddedt.archaicfix.occlusion;

import net.minecraft.client.renderer.WorldRenderer;

public interface IRenderGlobal {
    WorldRenderer getRenderer(int x, int y, int z);
    WorldRenderer getRenderer(double x, double y, double z);
    void pushWorkerRenderer(WorldRenderer wr);
    void internalMarkBlockUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

    void arch$setRendererUpdateOrderProvider(IRendererUpdateOrderProvider orderProvider);
    void arch$addRenderGlobalListener(IRenderGlobalListener listener);
}
