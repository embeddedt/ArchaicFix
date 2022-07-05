package org.embeddedt.archaicfix.occlusion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.embeddedt.archaicfix.occlusion.util.IntStack;

import java.util.*;

public class OcclusionHelpers {
    public static RenderWorker worker;

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

        public volatile boolean dirty = false;
        private ArrayDeque<CullInfo> queue = new ArrayDeque<CullInfo>();
        @SuppressWarnings("unused")
        private Frustrum fStack = new Frustrum();
        private IdentityHashMap<WorldRenderer, CullInfo> log = new IdentityHashMap<WorldRenderer, CullInfo>();
        private WorldClient theWorld;
        private Chunk[] chunkArray = null;
        private RenderGlobal render;

        private final Minecraft mc = Minecraft.getMinecraft();

        public void run(boolean immediate) {

            l: {
                if (render == null) {
                    return;
                }
                IRenderGlobal extendedRender = (IRenderGlobal)render;
                EntityLivingBase view = mc.renderViewEntity;
                if (theWorld == null || view == null) {
                    return;
                }
                theWorld.theProfiler.startSection("prep");
                WorldRenderer[] renderers = render.sortedWorldRenderers;
                for (int i = 0, e = render.renderersLoaded; i < e; ++i) {
                    renderers[i].isVisible = false;
                }
                render.renderersLoaded = 0;
                WorldRenderer center;
                RenderPosition back = RenderPosition.getBackFacingFromVector(view);
                WorldClient theWorld = this.theWorld;
                int renderDistanceChunks = render.renderDistanceChunks, renderDistanceWidth = renderDistanceChunks * 2 + 1;
                Chunk[] chunks;
                int chunkX, chunkZ;

                ArrayDeque<CullInfo> queue = this.queue;
                //Vector3 p_view = new Vector3();
                //Vector3 view_look = new Vector3();
                {
                    int x = MathHelper.floor_double(view.posX);
                    int y = MathHelper.floor_double(view.posY + view.getEyeHeight());
                    int z = MathHelper.floor_double(view.posZ);
                    //view_look.set(0, 0, -1);
                    //view_look.rotate(view.rotationPitch * (float) Math.PI / 180, Vector3.axes[3]);
                    //view_look.rotate(view.rotationYaw * (float) Math.PI / 180, Vector3.axes[1]);
                    //view_look.normalize();

                    //p_view.set(view_look).multiply(64).add(x, y, z);

                    theWorld.theProfiler.endStartSection("gather_chunks");
                    {
                        int t = ++renderDistanceWidth * renderDistanceWidth--;
                        chunks = chunkArray == null || chunkArray.length != t ? chunkArray = new Chunk[t] : chunkArray;

                        chunkX = (x >> 4) - renderDistanceChunks - 1;
                        chunkZ = (z >> 4) - renderDistanceChunks - 1;

                        for (int j2 = 0; j2 <= renderDistanceWidth; ++j2) {
                            int left = j2 * renderDistanceWidth;
                            for (int k2 = 0; k2 <= renderDistanceWidth; ++k2) {
                                Chunk chunk = theWorld.getChunkFromChunkCoords(j2 + chunkX, k2 + chunkZ);
                                chunks[left + k2] = chunk;
                            }
                        }
                    }

                    theWorld.theProfiler.endStartSection("seed_queue");
                    center = extendedRender.getRenderer(x, y, z);
                    if (center == null) {
                        int level = y > 5 ? 250 : 5;
                        center = extendedRender.getRenderer(x, level, z);
                        if (center == null) {
                            dirty = false;
                            break l;
                        }
                        RenderPosition pos = y < 5 ? RenderPosition.UP : RenderPosition.DOWN;
                        {
                            Chunk chunk = getChunk(chunks, center, chunkX, chunkZ, renderDistanceWidth);
                            CullInfo info = new CullInfo(center, chunk == null ? DUMMY : ((ICulledChunk)chunk).getVisibility()[center.posY >> 4], pos, -2);
                            info.facings.addAll(RenderPosition.SIDES);
                            info.facings.remove(pos);
                            log.put(center, info);
                            queue.add(info);
                        }
                        boolean allNull = false;
                        theWorld.theProfiler.startSection("gather_world");
                        for (int size = 1; !allNull; ++size) {
                            allNull = true;
                            for (int i = 0, j = size; i < size;) {
                                for (int k = 0; k < 4; ++k) {
                                    int xm = (k & 1) == 0 ? -1 : 1;
                                    int zm = (k & 2) == 0 ? -1 : 1;
                                    center = extendedRender.getRenderer(x + i * 16 * xm, level, z + j * 16 * zm);
                                    if (center == null || !center.isInFrustum) {
                                        continue;
                                    }
                                    allNull = false;
                                    Chunk chunk = getChunk(chunks, center, chunkX, chunkZ, renderDistanceWidth);
                                    CullInfo info = new CullInfo(center, chunk == null ? DUMMY : ((ICulledChunk)chunk).getVisibility()[center.posY >> 4], pos, -2);
                                    info.facings.addAll(RenderPosition.SIDES);
                                    info.facings.remove(pos);
                                    log.put(center, info);
                                    queue.add(info);
                                }
                                ++i;
                                --j;
                            }
                        }
                        theWorld.theProfiler.endSection();
                    } else {
                        Chunk chunk = getChunk(chunks, center, chunkX, chunkZ, renderDistanceWidth);
                        VisGraph sides;
                        if (chunk != null) {
                            sides = ((ICulledChunk)chunk).getVisibility()[center.posY >> 4];
                        } else {
                            sides = DUMMY;
                        }
                        {
                            markRenderer(center, view, sides);
                            CullInfo info = new CullInfo(center, sides, back, (renderDistanceChunks >> 1) * -1 - 3);
                            info.facings.remove(back);
                            log.put(center, info);
                        }

                        Set<EnumFacing> faces = sides.getVisibleFacingsFrom(x, y, z);
                        RenderPosition[] bias = RenderPosition.POSITIONS_BIAS[back.ordinal()];
                        for (int p = 0; p < 6; ++p) {
                            RenderPosition pos = bias[p];
                            if (!faces.contains(pos.facing))
                                continue;
                            WorldRenderer t = extendedRender.getRenderer(center.posX + pos.x, center.posY + pos.y, center.posZ + pos.z);

                            if (t == null || !t.isInFrustum)
                                continue;

                            chunk = getChunk(chunks, t, chunkX, chunkZ, renderDistanceWidth);

                            CullInfo info = new CullInfo(t, chunk == null ? DUMMY : ((ICulledChunk)chunk).getVisibility()[t.posY >> 4], pos, (renderDistanceChunks >> 1) * -1 - 2);
                            info.facings.remove(pos);
                            log.put(t, info);
                            queue.add(info);
                        }
                    }
                }

                theWorld.theProfiler.endStartSection("process_queue");
                if (!queue.isEmpty()) {
                    @SuppressWarnings("unused")
                    int visited = queue.size(), considered = visited;
                    //fStack.setPosition(p_view.x, p_view.y, p_view.z);
                    //Matrix4 view_m = new Matrix4(ClippingHelperImpl.instance.modelviewMatrix);
                    {
                        //float m = (float) Math.PI / 180;
                        //float v = (float) Math.cos(view.rotationPitch * m);
                        //view_m.camera(new Vector3(
                        //	v * (float) Math.sin((view.rotationYaw +180) * m),
                        //	-(float) Math.sin(view.rotationPitch * m),
                        //	v * (float) Math.cos((view.rotationYaw +180) * m)
                        //));
                    }
                    //Matrix4 view_p = new Matrix4(ClippingHelperImpl.instance.projectionMatrix);
                    //Vector3 p_chunk = new Vector3();
                    // TODO: frustrum stack: https://tomcc.github.io/frustum_clamping.html
                    IdentityHashMap<WorldRenderer, CullInfo> log = this.log;
                    RenderGlobal render = this.render;
                    RenderPosition[] bias = RenderPosition.POSITIONS_BIAS[back.ordinal() ^ 1];
                    for (; !queue.isEmpty();) {
                        CullInfo info = queue.pollFirst();
                        if (info == null) {
                            break;
                        }

                        info.visited = true;
                        if (info.count > renderDistanceChunks)
                            continue;

                        WorldRenderer rend = info.rend;
                        Chunk chunk = getChunk(chunks, rend, chunkX, chunkZ, renderDistanceWidth);

                        VisGraph sides;
                        if (chunk != null) {
                            sides = ((ICulledChunk)chunk).getVisibility()[rend.posY >> 4];
                        } else {
                            sides = DUMMY;
                        }
                        RenderPosition opp = info.last;

                        markRenderer(rend, view, sides);

                        //p_chunk.set(rend.posX + 8, rend.posY + 8, rend.posZ + 8);
                        //view_p.perspective(0, 0, (float) p_chunk.sub(p_view).mag(), 1250);
                        //fStack.set(view_m, view_p);

                        SetVisibility vis = sides.getVisibility();
                        boolean allVis = vis.isAllVisible(true);
                        for (int p = 0; p < 6; ++p) {
                            RenderPosition pos = bias[p];
                            if (pos == opp || info.facings.contains(pos))
                                continue;

                            if (allVis || vis.isVisible(opp.facing, pos.facing)) {
                                info.facings.add(pos);

                                WorldRenderer t = extendedRender.getRenderer(rend.posX + pos.x, rend.posY + pos.y, rend.posZ + pos.z);
                                if (t != null && t.isInFrustum) {
                                    ++considered;
                                    int cost = 1;

                                    if (pos == back) {
                                        cost += renderDistanceChunks;
                                    }

                                    CullInfo prev = log.get(t);
                                    if (prev != null) {
                                        if (prev.facings.contains(pos)) {
                                            continue;
                                        }

                                        if (!prev.visited) {
                                            if (prev.vis.getVisibility().isVisible(pos.facing, prev.last.facing)) {
                                                continue;
                                            }
                                        }
                                    }

                                    //if (!fStack.isBoundingBoxInFrustum(t.rendererBoundingBox))
                                    //continue;

                                    if (t.isWaitingOnOcclusionQuery | allVis) {
                                        cost -= renderDistanceChunks >> 1;
                                        cost &= ~cost >> 31;
                                    }

                                    ++visited;
                                    CullInfo data;
                                    {
                                        VisGraph oSides;
                                        if (prev == null) {
                                            Chunk o;
                                            if (t.posX != rend.posX | t.posZ != rend.posZ) {
                                                o = getChunk(chunks, t, chunkX, chunkZ, renderDistanceWidth);
                                            } else
                                                o = chunk;
                                            if (o == null) {
                                                oSides = DUMMY;
                                            } else {
                                                oSides = ((ICulledChunk)o).getVisibility()[t.posY >> 4];
                                            }
                                        } else {
                                            oSides = prev.vis;
                                        }
                                        data = new CullInfo(t, oSides, pos, info.count + cost);
                                    }

                                    if (prev != null) {
                                        data.facings.addAll(prev.facings);
                                    }

                                    log.put(t, data);
                                    queue.add(data);
                                }
                            }
                        }
                    }
                }
                theWorld.theProfiler.endStartSection("cleanup");
                queue.clear();
                log.clear();
            }
            dirty = false;
            theWorld.theProfiler.endSection();
        }

        private void markRenderer(WorldRenderer rend, EntityLivingBase view, VisGraph vis) {

            if (!rend.isVisible) {
                rend.isVisible = true;
                if (!rend.isWaitingOnOcclusionQuery) {
                    // only add it to the list of sorted renderers if it's not skipping all passes (re-used field)
                    render.sortedWorldRenderers[render.renderersLoaded++] = rend;
                }
            }
            if (!rend.isInitialized | rend.needsUpdate || vis.isRenderDirty()) {
                rend.needsUpdate = true;
                if (!rend.isInitialized || (rend.needsUpdate && rend.distanceToEntitySquared(view) <= 1128.0F)) {
                    ((IRenderGlobal)render).pushWorkerRenderer(rend);
                }
            }
        }

        private static Chunk getChunk(Chunk[] chunks, WorldRenderer rend, int chunkX, int chunkZ, int renderDistanceWidth) {

            int x = (rend.posX >> 4) - chunkX, z = (rend.posZ >> 4) - chunkZ;
            if (x < 0 | z < 0 | x > renderDistanceWidth | z > renderDistanceWidth) {
                return null;
            }
            return chunks[x * renderDistanceWidth + z];
        }

        private static class CullInfo {

            boolean visited = false;
            final int count;
            final WorldRenderer rend;
            final VisGraph vis;
            final RenderPosition last;
            final EnumSet<RenderPosition> facings;

            public CullInfo(WorldRenderer rend, VisGraph vis, RenderPosition pos, int count) {

                this.count = count;
                this.rend = rend;
                this.vis = vis;
                this.last = pos.getOpposite();
                this.facings = EnumSet.of(last);
            }

        }
    }

    private static enum RenderPosition {
        DOWN(EnumFacing.DOWN, 0, -1, 0),
        UP(EnumFacing.UP, 0, 16, 0),
        WEST(EnumFacing.WEST, -1, 0, 0),
        EAST(EnumFacing.EAST, 16, 0, 0),
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
            RenderPosition[] values = values();
            int i = values.length;

            for (int j = 0; j < i; ++j) {
                RenderPosition face = values[j];
                float cur = x * -face._x + y * -face._y + z * -face._z;

                if (cur > max) {
                    max = cur;
                    ret = face;
                }
            }

            return ret;
        }
    }
}
