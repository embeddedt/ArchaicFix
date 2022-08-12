package org.embeddedt.archaicfix.occlusion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.embeddedt.archaicfix.occlusion.util.IntStack;
import org.embeddedt.archaicfix.occlusion.util.RecyclingList;

import java.util.*;

public class OcclusionHelpers {
    public static RenderWorker worker;
    public static long chunkUpdateDeadline;
    public static float partialTickTime;

    public static final boolean DEBUG_ALWAYS_RUN_OCCLUSION = Boolean.parseBoolean(System.getProperty("archaicfix.debug.alwaysRunOcclusion", "false"));
    public static final boolean DEBUG_PRINT_QUEUE_ITERATIONS = Boolean.parseBoolean(System.getProperty("archaicfix.debug.printQueueIterations", "false"));
    /** Update up to 1 chunk per frame when the framerate is uncapped, vanilla-style. */
    public static final boolean DEBUG_LAZY_CHUNK_UPDATES = Boolean.parseBoolean(System.getProperty("archaicfix.debug.lazyChunkUpdates", "false"));

    public static void init() {
        worker = new RenderWorker();
    }

    public static IntStack deferredAreas = new IntStack(6 * 1024);

    public static synchronized void updateArea(int x, int y, int z, int x2, int y2, int z2) {

        // backwards so it's more logical to extract
        deferredAreas.add(z2);
        deferredAreas.add(y2);
        deferredAreas.add(x2);
        deferredAreas.add(z);
        deferredAreas.add(y);
        deferredAreas.add(x);
    }

    public static synchronized void processUpdate(IRenderGlobal render) {

        if (deferredAreas.isEmpty()) {
            return; // guard against multiple instances (no compatibility with mods that do this to us)
        }

        int x = deferredAreas.pop(), y = deferredAreas.pop(), z = deferredAreas.pop();
        int x2 = deferredAreas.pop(), y2 = deferredAreas.pop(), z2 = deferredAreas.pop();
        render.internalMarkBlockUpdate(x, y, z, x2, y2, z2);
    }

    public static void updateRendererNeighbors(RenderGlobal rg, WorldRenderer[] worldRenderers, int renderChunksWide, int renderChunksDeep, int renderChunksTall) {
        if(worldRenderers == null) return;
        for(WorldRenderer rend : worldRenderers) {
            for(EnumFacing dir : EnumFacing.values()) {
                ((IWorldRenderer)rend).arch$setNeighbor(dir, ((IRenderGlobal)rg).getRenderer(
                        rend.posX + dir.getFrontOffsetX() * 16,
                        rend.posY + dir.getFrontOffsetY() * 16,
                        rend.posZ + dir.getFrontOffsetZ() * 16
                ));
            }
        }
    }

    public static class RenderWorker {

        public RenderWorker() {

			/*for (int i = 0; i < fStack.length; ++i) {
				fStack[i] = new Frustrum();
			}//*/
        }

        public void setWorld(RenderGlobal rg, WorldClient world) {

            render = rg;
            theWorld = world;
        }

        private static VisGraph DUMMY = new VisGraph();
        static {
            DUMMY.computeVisibility();
        }

        private static RecyclingList<CullInfo> cullInfoBuf = new RecyclingList<>(() -> new CullInfo());

        public volatile boolean dirty = false;
        public int dirtyFrustumRenderers;
        private int frame = 0;
        private ArrayDeque<CullInfo> queue = new ArrayDeque<CullInfo>();
        @SuppressWarnings("unused")
        private Frustrum fStack = new Frustrum();
        private WorldClient theWorld;
        private ChunkCache chunkCache = new ChunkCache();
        private RenderGlobal render;

        private final Minecraft mc = Minecraft.getMinecraft();

        public void run(boolean immediate) {
            frame++;
            queue.clear();
            cullInfoBuf.reset();
            int queueIterations = 0;

            if (render == null) {
                return;
            }
            EntityLivingBase view = mc.renderViewEntity;
            if (theWorld == null || view == null) {
                return;
            }

            Frustrum frustum = getFrustum();

            theWorld.theProfiler.startSection("prep");

            prepareRenderers();

            RenderPosition back = RenderPosition.getBackFacingFromVector(view);

            seedQueue(frustum);

            theWorld.theProfiler.endStartSection("process_queue");
            while(!queue.isEmpty()) {
                queueIterations++;
                CullInfo info = queue.pollFirst();

                markRenderer(info, view);

                for (RenderPosition stepPos : CullInfo.ALLOWED_STEPS[info.facings & 0b111111]) {
                    if(canStep(info, stepPos)) {
                        maybeEnqueueNeighbor(info, stepPos, queue, frustum);
                    }
                }
            }
            theWorld.theProfiler.endStartSection("cleanup");
            queue.clear();

            if(DEBUG_PRINT_QUEUE_ITERATIONS && queueIterations != 0){
                System.out.println("queue iterations: " + queueIterations);
            }
            dirty = false;
            theWorld.theProfiler.endSection();
        }

        private void prepareRenderers() {
            WorldRenderer[] renderers = render.sortedWorldRenderers;

            for (int i = 0; i < render.worldRenderers.length; ++i) {
                render.worldRenderers[i].isVisible = false;
            }
            render.renderersLoaded = 0;
        }

        private Frustrum getFrustum() {
            EntityLivingBase view = mc.renderViewEntity;

            Frustrum frustum = new Frustrum();
            // TODO: interpolate using partial tick time
            frustum.setPosition(view.posX, view.posY, view.posZ);
            return frustum;
        }

        private void seedQueue(Frustrum frustum) {
            int renderDistanceChunks = render.renderDistanceChunks;
            EntityLivingBase view = mc.renderViewEntity;

            int viewX = MathHelper.floor_double(view.posX);
            int viewY = MathHelper.floor_double(view.posY + view.getEyeHeight());
            int viewZ = MathHelper.floor_double(view.posZ);

            theWorld.theProfiler.endStartSection("gather_chunks");
            chunkCache.gatherChunks(theWorld, viewX >> 4, viewZ >> 4, renderDistanceChunks);

            IRenderGlobal extendedRender = (IRenderGlobal)render;

            theWorld.theProfiler.endStartSection("seed_queue");

            WorldRenderer center = extendedRender.getRenderer(viewX, viewY, viewZ);
            isInFrustum(center, frustum); // make sure frustum status gets updated for the starting renderer
            if (center != null) {
                Chunk chunk = chunkCache.getChunk(center);
                VisGraph sides = isChunkEmpty(chunk) ? DUMMY : ((ICulledChunk)chunk).getVisibility()[center.posY >> 4];
                CullInfo info = cullInfoBuf.next().init(center, sides, RenderPosition.NONE, renderDistanceChunks * -1 - 3);
                markRenderer(info, view);
                queue.add(info);
            } else {
                int level = viewY > 5 ? 250 : 5;
                center = extendedRender.getRenderer(viewX, level, viewZ);
                if (center != null) {
                    RenderPosition pos = viewY < 5 ? RenderPosition.UP : RenderPosition.DOWN;
                    {
                        Chunk chunk = chunkCache.getChunk(center);
                        CullInfo info = cullInfoBuf.next().init(center, isChunkEmpty(chunk) ? DUMMY : ((ICulledChunk) chunk).getVisibility()[center.posY >> 4], RenderPosition.NONE, -2);
                        queue.add(info);
                    }
                    boolean allNull = false;
                    theWorld.theProfiler.startSection("gather_world");
                    for (int size = 1; !allNull; ++size) {
                        allNull = true;
                        for (int i = 0, j = size; i < size; ) {
                            for (int k = 0; k < 4; ++k) {
                                int xm = (k & 1) == 0 ? -1 : 1;
                                int zm = (k & 2) == 0 ? -1 : 1;
                                center = extendedRender.getRenderer(viewX + i * 16 * xm, level, viewZ + j * 16 * zm);
                                if (!isInFrustum(center, frustum)) {
                                    continue;
                                }
                                allNull = false;
                                Chunk chunk = chunkCache.getChunk(center);
                                CullInfo info = cullInfoBuf.next().init(center, isChunkEmpty(chunk) ? DUMMY : ((ICulledChunk) chunk).getVisibility()[center.posY >> 4], RenderPosition.NONE, -2);
                                queue.add(info);
                            }
                            ++i;
                            --j;
                        }
                    }
                    theWorld.theProfiler.endSection();
                }
            }
        }

        private boolean canStep(CullInfo info, RenderPosition stepPos) {
            boolean allVis = mc.playerController.currentGameType.getID() == 3;

            if (!allVis && !SetVisibility.isVisible(info.vis.getVisibility(), info.dir.getOpposite().facing, stepPos.facing)) {
                return false;
            }

            return true;
        }
        
        private void maybeEnqueueNeighbor(CullInfo info, RenderPosition stepPos, Queue queue, Frustrum frustum) {
            WorldRenderer t = ((IWorldRenderer)info.rend).arch$getNeighbor(stepPos.facing);
            IWorldRenderer extendedT = (IWorldRenderer) t;

            if (t == null || !extendedT.arch$setLastCullUpdateFrame(frame) || !isInFrustum(t, frustum))
                return;

            int cost = 0;

            Chunk o = chunkCache.getChunk(t);
            VisGraph oSides = isChunkEmpty(o) ? DUMMY : ((ICulledChunk)o).getVisibility()[t.posY >> 4];
            CullInfo data = cullInfoBuf.next().init(t, oSides, stepPos, info.cost + cost);

            data.facings |= info.facings;

            queue.add(data);
        }

        private void markRenderer(CullInfo info, EntityLivingBase view) {
            WorldRenderer rend = info.rend;
            if (!rend.isVisible) {
                rend.isVisible = true;
                if (!rend.isWaitingOnOcclusionQuery) {
                    // only add it to the list of sorted renderers if it's not skipping all passes (re-used field)
                    render.sortedWorldRenderers[render.renderersLoaded++] = rend;
                }
            }
            if (rend.needsUpdate || !rend.isInitialized || info.vis.isRenderDirty()) {
                rend.needsUpdate = true;
                if (!rend.isInitialized || (rend.needsUpdate && rend.distanceToEntitySquared(view) <= 1128.0F)) {
                    ((IRenderGlobal)render).pushWorkerRenderer(rend);
                }
            }
        }

        private static boolean isInFrustum(WorldRenderer r, Frustrum frustum){
            if(r != null) {
                if(r.isInFrustum) {
                    /** Defer checking if visible renderers are still in the frustum */
                    ((IWorldRenderer)r).arch$setIsFrustumCheckPending(true);
                } else {
                    r.updateInFrustum(frustum);
                }
            }
            return r != null && r.isInFrustum;
        }

        private static boolean isChunkEmpty(Chunk chunk) {
            return chunk == null || chunk.isEmpty();
        }

        private static class CullInfo {

            public static final RenderPosition[][] ALLOWED_STEPS;

            static {
                ALLOWED_STEPS = generateAllowedSteps();
            }

            private static RenderPosition[][] generateAllowedSteps() {
                RenderPosition[][] allowedSteps = new RenderPosition[(int) Math.pow(2, 6) + 1][];

                for (int xStep = -1; xStep <= 1; xStep++) {
                    for (int yStep = -1; yStep <= 1; yStep++) {
                        for (int zStep = -1; zStep <= 1; zStep++) {
                            byte mask = 0;

                            //                    SNEWUD
                            mask |= new byte[]{ 0b000100,
                                                0b000000,
                                                0b001000
                            }[xStep + 1];

                            //                    SNEWUD
                            mask |= new byte[]{ 0b000001,
                                                0b000000,
                                                0b000010
                            }[yStep + 1];

                            //                    SNEWUD
                            mask |= new byte[]{ 0b010000,
                                                0b000000,
                                                0b100000
                            }[zStep + 1];

                            byte finalMask = mask;
                            allowedSteps[mask] =
                                    Arrays.asList(RenderPosition.DOWN, RenderPosition.UP, RenderPosition.NORTH, RenderPosition.SOUTH, RenderPosition.WEST, RenderPosition.EAST)
                                            .stream()
                                            .filter(p -> (1 << (p.getOpposite().ordinal()) & finalMask) == 0)
                                            .toArray(RenderPosition[]::new);
                        }
                    }
                }
                return allowedSteps;
            }

            int cost;
            WorldRenderer rend;
            VisGraph vis;
            /** The direction we stepped in to reach this subchunk. */
            RenderPosition dir;
            /** All the directions we have stepped in to reach this subchunk. */
            byte facings;

            public CullInfo() {

            }

            public CullInfo init(WorldRenderer rend, VisGraph vis, RenderPosition dir, int cost) {
                this.cost = cost;
                this.rend = rend;
                this.vis = vis;
                this.dir = dir;
                this.facings = 0;
                this.facings |= (1 << this.dir.ordinal());

                return this;
            }

        }

        private static class ChunkCache {

            private World theWorld;
            private Chunk[] chunkCache = null;
            private int cornerX, cornerZ, size;

            public void gatherChunks(World theWorld, int centerChunkX, int centerChunkZ, int renderDistanceChunks) {
                this.size = renderDistanceChunks * 2 + 1;
                this.cornerX = centerChunkX - renderDistanceChunks - 1;
                this.cornerZ = centerChunkZ - renderDistanceChunks - 1;
                this.theWorld = theWorld;

                int length = (size + 1) * (size + 1);
                Chunk[] chunks = chunkCache == null || chunkCache.length != length ? chunkCache = new Chunk[length] : chunkCache;

                for (int x = 0; x <= size; ++x) {
                    int columnStart = x * size;
                    for (int z = 0; z <= size; ++z) {
                        Chunk chunk = theWorld.getChunkFromChunkCoords(x + cornerX, z + cornerZ);
                        chunks[columnStart + z] = chunk;
                    }
                }
            }

            public Chunk getChunk(WorldRenderer rend) {
                int x = (rend.posX >> 4) - cornerX, z = (rend.posZ >> 4) - cornerZ;
                if (x < 0 | z < 0 | x > size | z > size) {
                    return null;
                }
                return chunkCache[x * size + z];
            }

        }
    }

    public static enum RenderPosition {
        // EnumFacing.EAST and EnumFacing.WEST is flipped in MCP
        DOWN(EnumFacing.DOWN, 0, -1, 0),
        UP(EnumFacing.UP, 0, 16, 0),
        WEST(EnumFacing.EAST /* WEST */, -1, 0, 0),
        EAST(EnumFacing.WEST /* EAST */, 16, 0, 0),
        NORTH(EnumFacing.NORTH, 0, 0, -1),
        SOUTH(EnumFacing.SOUTH, 0, 0, 16),
        NONE(null, 0, 0, 0),
        NONE_opp(null, 0, 0, 0);

        public static final RenderPosition[] POSITIONS = values();
        public static final RenderPosition[][] POSITIONS_BIAS = new RenderPosition[6][6];
        public static final RenderPosition[] FROM_FACING = new RenderPosition[6];
        public static final List<RenderPosition> SIDES = Arrays.asList(POSITIONS).subList(1, 6);
        static {
            for (int i = 0; i < 6; ++i) {
                RenderPosition pos = POSITIONS[i];
                FROM_FACING[pos.facing.ordinal()] = pos;
                RenderPosition[] bias = POSITIONS_BIAS[i];
                int j = 0, xor = pos.ordinal() & 1;
                switch (pos) {
                    case DOWN:
                    case UP:
                        bias[j++] = pos;
                        bias[j++] = POSITIONS[NORTH.ordinal() ^ xor];
                        bias[j++] = POSITIONS[SOUTH.ordinal() ^ xor];
                        bias[j++] = POSITIONS[EAST.ordinal() ^ xor];
                        bias[j++] = POSITIONS[WEST.ordinal() ^ xor];
                        bias[j++] = pos.getOpposite();
                        break;
                    case WEST:
                    case EAST:
                        bias[j++] = pos;
                        bias[j++] = POSITIONS[NORTH.ordinal() ^ xor];
                        bias[j++] = POSITIONS[SOUTH.ordinal() ^ xor];
                        bias[j++] = POSITIONS[UP.ordinal() ^ xor];
                        bias[j++] = POSITIONS[DOWN.ordinal() ^ xor];
                        bias[j++] = pos.getOpposite();
                        break;
                    case NORTH:
                    case SOUTH:
                        bias[j++] = pos;
                        bias[j++] = POSITIONS[EAST.ordinal() ^ xor];
                        bias[j++] = POSITIONS[WEST.ordinal() ^ xor];
                        bias[j++] = POSITIONS[UP.ordinal() ^ xor];
                        bias[j++] = POSITIONS[DOWN.ordinal() ^ xor];
                        bias[j++] = pos.getOpposite();
                        break;
                    case NONE:
                    case NONE_opp:
                        break;
                }
            }
        }

        public final int x, y, z;
        public final EnumFacing facing;

        RenderPosition(EnumFacing face, int x, int y, int z) {

            this.facing = face;
            this.x = x;
            this.y = y;
            this.z = z;
            _x = x > 0 ? 1 : x;
            _y = y > 0 ? 1 : y;
            _z = z > 0 ? 1 : z;
        }

        public RenderPosition getOpposite() {

            return POSITIONS[ordinal() ^ 1];
        }

        private final int _x, _y, _z;

        private static final RenderPosition[] VALUES = values();

        public static RenderPosition getBackFacingFromVector(EntityLivingBase e) {

            float x, y, z;
            {
                float f = e.rotationPitch;
                float f1 = e.rotationYaw;

                if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
                {
                    f += 180.0F;
                }

                float f2 = MathHelper.cos(-f1 * 0.017453292F - (float) Math.PI);
                float f3 = MathHelper.sin(-f1 * 0.017453292F - (float) Math.PI);
                float f4 = -MathHelper.cos(-f * 0.017453292F);
                float f5 = MathHelper.sin(-f * 0.017453292F);
                x = f3 * f4;
                y = f5;
                z = f2 * f4;
            }
            RenderPosition ret = NORTH;
            float max = Float.MIN_VALUE;
            int i = VALUES.length;

            for (int j = 0; j < i; ++j) {
                RenderPosition face = VALUES[j];
                float cur = x * -face._x + y * -face._y + z * -face._z;

                if (cur > max) {
                    max = cur;
                    ret = face;
                }
            }

            return ret;
        }
    }

    private static int positiveMod(int val, int div) {
        int rem = val % div;
        if(rem < 0) rem += div;
        return rem;
    }
}
