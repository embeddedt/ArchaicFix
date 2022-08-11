package org.embeddedt.archaicfix.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import org.embeddedt.archaicfix.occlusion.IRenderGlobal;
import org.embeddedt.archaicfix.occlusion.IRendererUpdateOrderProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadedChunkUpdateHelper {

    public static ThreadedChunkUpdateHelper instance;

    ExecutorService executor = Executors.newFixedThreadPool(1);

    private UpdateTask currentTask = new UpdateTask(); // only 1 task at a time for now

    IRendererUpdateOrderProvider rendererUpdateOrderProvider = new IRendererUpdateOrderProvider() {
        @Override
        public void prepare(List<WorldRenderer> worldRenderersToUpdateList) {
            preRendererUpdates(worldRenderersToUpdateList);
        }

        @Override
        public boolean hasNext(List<WorldRenderer> worldRenderersToUpdateList) {
            return currentTask.future != null && currentTask.future.isDone();
        }

        @Override
        public WorldRenderer next(List<WorldRenderer> worldRenderersToUpdateList) {
            WorldRenderer wr = currentTask.renderer;
            currentTask.future = null;
            currentTask.renderer = null;
            return wr;
        }

        @Override
        public void cleanup(List<WorldRenderer> worldRenderersToUpdateList) {

        }
    };

    public void init() {
        ((IRenderGlobal) Minecraft.getMinecraft().renderGlobal).arch$setRendererUpdateOrderProvider(rendererUpdateOrderProvider);
    }

    private void preRendererUpdates(List<WorldRenderer> toUpdateList) {
        if(!toUpdateList.isEmpty()) {
            WorldRenderer wr = toUpdateList.get(0);
            if(currentTask.future == null) {
                submitUpdateTask(wr);
            }
        }
    }

    private void submitUpdateTask(WorldRenderer wr) {
        currentTask.future = executor.submit(() -> {
            doChunkUpdate(wr);
            return null;
        });
        currentTask.renderer = wr;
    }

    public void doChunkUpdate(WorldRenderer wr) {
        // TODO
        System.out.println("Updating renderer " + wr.posX + " " + wr.posY + " " + wr.posZ + "...");
        try {
            Thread.sleep(60 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        // TODO: destroy state when chunks are reloaded or server is stopped
    }

    private static class UpdateTask {
        Future<Void> future;
        WorldRenderer renderer;
    }
}
