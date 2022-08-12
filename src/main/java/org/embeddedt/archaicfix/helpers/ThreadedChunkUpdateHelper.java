package org.embeddedt.archaicfix.helpers;

import lombok.SneakyThrows;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.world.ChunkCache;
import org.embeddedt.archaicfix.occlusion.IRenderGlobal;
import org.embeddedt.archaicfix.occlusion.IRendererUpdateOrderProvider;
import org.embeddedt.archaicfix.threadedupdates.ICapturableTessellator;
import org.embeddedt.archaicfix.threadedupdates.IRendererUpdateResultHolder;
import scala.collection.script.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadedChunkUpdateHelper {

    public static ThreadedChunkUpdateHelper instance;

    public static Thread MAIN_THREAD;

    /** Used within the scope of WorldRenderer#updateWorld (on the main thread). */
    public static UpdateTask.Result lastUpdateResult;

    // TODO use more threads
    ExecutorService executor = Executors.newFixedThreadPool(1);

    private UpdateTask currentTask = new UpdateTask(); // only 1 task at a time for now
    public Tessellator threadTessellator; // again, only 1 for now

    IRendererUpdateOrderProvider rendererUpdateOrderProvider = new IRendererUpdateOrderProvider() {
        private List<WorldRenderer> updatedRenderers = new ArrayList<>(); // the renderers updated during the batch

        @Override
        public void prepare(List<WorldRenderer> worldRenderersToUpdateList) {
            preRendererUpdates(worldRenderersToUpdateList);
        }

        @Override
        public boolean hasNext(List<WorldRenderer> worldRenderersToUpdateList) {
            return currentTask.future != null && currentTask.future.isDone();
        }

        @SneakyThrows
        @Override
        public WorldRenderer next(List<WorldRenderer> worldRenderersToUpdateList) {
            WorldRenderer wr = currentTask.renderer;
            ((IRendererUpdateResultHolder)wr).arch$setRendererUpdateResult(currentTask.future.get());
            currentTask.renderer = null;
            currentTask.future = null;

            updatedRenderers.add(wr);

            return wr;
        }

        @Override
        public void cleanup(List<WorldRenderer> worldRenderersToUpdateList) {
            worldRenderersToUpdateList.removeAll(updatedRenderers);
            updatedRenderers.clear();
        }
    };

    public void init() {
        ((IRenderGlobal) Minecraft.getMinecraft().renderGlobal).arch$setRendererUpdateOrderProvider(rendererUpdateOrderProvider);
        threadTessellator = new Tessellator();
        MAIN_THREAD = Thread.currentThread();
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
        currentTask.future = executor.submit(() -> doChunkUpdate(wr));
        currentTask.renderer = wr;
    }

    /** Renders certain blocks (as defined in canBlockBeRenderedOffThread) on the thread, and saves the tessellation
     *  result. WorldRenderer#updateRenderer will skip over these blocks, and use the result produced by the thread to
     *  fill them in.
     */
    // TODO if the chunk is modified during the update, schedule a re-update (maybe interrupt the update too).
    public UpdateTask.Result doChunkUpdate(WorldRenderer wr) {
        UpdateTask.Result result = new UpdateTask.Result();
        //System.out.println("Updating renderer " + wr.posX + " " + wr.posY + " " + wr.posZ + "...");

        /*ChunkCache chunkcache = getChunkCacheSnapshot(wr);
        if(!chunkcache.extendedLevelsInChunkCache()) {
            RenderBlocks renderblocks = new RenderBlocks(chunkcache);

            int pass = 0;
            boolean renderedSomething = false;
            boolean startedTessellator = false;

            for (int y = wr.posY; y < wr.posY + 16; ++y) {
                for (int z = wr.posZ; z < wr.posZ + 16; ++z) {
                    for (int x = wr.posX; x < wr.posX + 16; ++x) {
                        Block block = chunkcache.getBlock(x, y, z);

                        if (block.getMaterial() != Material.air) {
                            if (!startedTessellator) {
                                startedTessellator = true;
                                threadTessellator.startDrawingQuads(); // TODO triangulator compat
                                threadTessellator.setTranslation((double)(-wr.posX), (double)(-wr.posY), (double)(-wr.posZ));
                            }

                            int k3 = block.getRenderBlockPass();

                            if (!block.canRenderInPass(pass)) continue;

                            if(canBlockBeRenderedOffThread(block, pass)) {
                                renderedSomething |= renderblocks.renderBlockByRenderType(block, x, y, z);
                            }
                        }
                    }
                }
            }

            if(startedTessellator) {
                result.renderedQuads = ((ICapturableTessellator)threadTessellator).arch$getUnsortedVertexState();
            }
            result.renderedSomething = renderedSomething;
        }*/

        /*try {
            Thread.sleep(60 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return result;
    }

    public static boolean canBlockBeRenderedOffThread(Block block, int pass) {
        return block.getRenderType() == 0;
    }

    private ChunkCache getChunkCacheSnapshot(WorldRenderer wr) {
        // TODO This is not thread safe! Actually make a snapshot here.
        byte pad = 1;
        ChunkCache chunkcache = new ChunkCache(wr.worldObj, wr.posX - pad, wr.posY - pad, wr.posZ - pad,
                wr.posX + 16 + pad, wr.posY + 16 + pad, wr.posZ + 16 + pad, pad);
        return chunkcache;
    }

    public void clear() {
        // TODO: destroy state when chunks are reloaded or server is stopped
    }

    public static class UpdateTask {
        Future<Result> future;
        WorldRenderer renderer;

        public static class Result {
            public boolean renderedSomething;
            public TesselatorVertexState renderedQuads;
        }
    }
}
