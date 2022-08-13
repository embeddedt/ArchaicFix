package org.embeddedt.archaicfix.helpers;

import static org.embeddedt.archaicfix.ArchaicLogger.LOGGER;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadedChunkUpdateHelper {

    public static ThreadedChunkUpdateHelper instance;

    public static Thread MAIN_THREAD;

    /** Used within the scope of WorldRenderer#updateWorld (on the main thread). */
    public static WorldRenderer lastWorldRenderer;

    /** Tasks not yet started */
    public BlockingQueue<WorldRenderer> taskQueue = new LinkedBlockingDeque<>();
    /** Finished tasks ready for consumption */
    public BlockingQueue<WorldRenderer> finishedTasks = new LinkedBlockingDeque<>();

    public ThreadLocal<Tessellator> threadTessellator = ThreadLocal.withInitial(Tessellator::new);

    IRendererUpdateOrderProvider rendererUpdateOrderProvider = new IRendererUpdateOrderProvider() {
        /** The renderers updated during the batch */
        private List<WorldRenderer> updatedRenderers = new ArrayList<>();

        @Override
        public void prepare(List<WorldRenderer> worldRenderersToUpdateList) {
            preRendererUpdates(worldRenderersToUpdateList);
        }

        @Override
        public boolean hasNext(List<WorldRenderer> worldRenderersToUpdateList) {
            return !finishedTasks.isEmpty();
        }

        @SneakyThrows
        @Override
        public WorldRenderer next(List<WorldRenderer> worldRenderersToUpdateList) {
            WorldRenderer wr = finishedTasks.take();
            updatedRenderers.add(wr);

            return wr;
        }

        @Override
        public void cleanup(List<WorldRenderer> worldRenderersToUpdateList) {
            for(WorldRenderer wr : updatedRenderers) {
                worldRenderersToUpdateList.remove(wr);
                ((IRendererUpdateResultHolder)wr).arch$getRendererUpdateTask().clear();
            }
            updatedRenderers.clear();
        }
    };

    public void init() {
        ((IRenderGlobal) Minecraft.getMinecraft().renderGlobal).arch$setRendererUpdateOrderProvider(rendererUpdateOrderProvider);
        MAIN_THREAD = Thread.currentThread();

        for(int i = 0; i < 1; i++) {
            new Thread(this::runThread, "Chunk Update Worker Thread #" + i).start();
        }
    }

    private void preRendererUpdates(List<WorldRenderer> toUpdateList) {
        updateWorkQueue(toUpdateList);
    }

    private void updateWorkQueue(List<WorldRenderer> toUpdateList) {
        final int updateQueueSize = 64; // TODO decide this dynamically
        taskQueue.clear();
        for(int i = 0; i < updateQueueSize && i < toUpdateList.size(); i++) {
            WorldRenderer wr = toUpdateList.get(i);
            UpdateTask task = ((IRendererUpdateResultHolder)wr).arch$getRendererUpdateTask();
            if(task.isEmpty()) {
                // No update in progress; add to task queue
                task.chunkCache = getChunkCacheSnapshot(wr);
                taskQueue.add(wr);
            }
        }
    }

    @SneakyThrows
    private void runThread() {
        while(true) {
            WorldRenderer wr = taskQueue.take();
            ((IRendererUpdateResultHolder)wr).arch$getRendererUpdateTask().started = true;
            try {
                doChunkUpdate(wr);
            } catch(Exception e) {
                LOGGER.error("Failed to update chunk " + worldRendererToString(wr));
                e.printStackTrace();
                for(UpdateTask.Result r : ((IRendererUpdateResultHolder) wr).arch$getRendererUpdateTask().result) {
                    r.clear();
                }
            }
            finishedTasks.add(wr);
        }
    }

    /** Renders certain blocks (as defined in canBlockBeRenderedOffThread) on the worker thread, and saves the
     *  tessellation result. WorldRenderer#updateRenderer will skip over these blocks, and use the result that was
     *  produced by the worker thread to fill them in.
     */
    // TODO if the chunk is modified during the update, schedule a re-update (maybe interrupt the update too).
    public void doChunkUpdate(WorldRenderer wr) {
        UpdateTask task = ((IRendererUpdateResultHolder)wr).arch$getRendererUpdateTask();

        ChunkCache chunkcache = task.chunkCache;

        if(!chunkcache.extendedLevelsInChunkCache()) {
            RenderBlocks renderblocks = new RenderBlocks(chunkcache);

            for(int pass = 0; pass < 2; pass++) {
                boolean renderedSomething = false;
                boolean startedTessellator = false;

                for (int y = wr.posY; y < wr.posY + 16; ++y) {
                    for (int z = wr.posZ; z < wr.posZ + 16; ++z) {
                        for (int x = wr.posX; x < wr.posX + 16; ++x) {
                            Block block = chunkcache.getBlock(x, y, z);

                            if (block.getMaterial() != Material.air) {
                                if (!startedTessellator) {
                                    startedTessellator = true;
                                    threadTessellator.get().startDrawingQuads(); // TODO triangulator compat
                                    threadTessellator.get().setTranslation((double) (-wr.posX), (double) (-wr.posY), (double) (-wr.posZ));
                                }

                                int k3 = block.getRenderBlockPass();

                                if (!block.canRenderInPass(pass)) continue;

                                if (canBlockBeRenderedOffThread(block, pass)) {
                                    renderedSomething |= renderblocks.renderBlockByRenderType(block, x, y, z);
                                }
                            }
                        }
                    }
                }

                if (startedTessellator) {
                    task.result[pass].renderedQuads = ((ICapturableTessellator) threadTessellator.get()).arch$getUnsortedVertexState();
                    ((ICapturableTessellator) threadTessellator.get()).discard();
                }
                task.result[pass].renderedSomething = renderedSomething;
            }
        }
    }

    public static boolean canBlockBeRenderedOffThread(Block block, int pass) {
        return block.getRenderType() < 42 && block.getRenderType() != 22; // vanilla block
    }

    private ChunkCache getChunkCacheSnapshot(WorldRenderer wr) {
        // TODO This is not thread-safe! Actually make a snapshot here.
        byte pad = 1;
        ChunkCache chunkcache = new ChunkCache(wr.worldObj, wr.posX - pad, wr.posY - pad, wr.posZ - pad,
                wr.posX + 16 + pad, wr.posY + 16 + pad, wr.posZ + 16 + pad, pad);
        return chunkcache;
    }

    public void clear() {
        // TODO: destroy state when chunks are reloaded or server is stopped
    }

    private static String worldRendererToString(WorldRenderer wr) {
        return "(" + wr.posX + ", " + wr.posY + ", " + wr.posZ + ")";
    }

    // Not sure how thread-safe this class is...
    public static class UpdateTask {
        public boolean started;
        public Result[] result = new Result[]{new Result(), new Result()};

        public ChunkCache chunkCache;

        public boolean isEmpty() {
            return !started;
        }

        public void clear() {
            started = false;
            chunkCache = null;
            for(Result r : result) {
                r.clear();
            }
        }

        public static class Result {
            public boolean renderedSomething;
            public TesselatorVertexState renderedQuads;

            public void clear() {
                renderedSomething = false;
                renderedQuads = null;
            }
        }
    }
}
