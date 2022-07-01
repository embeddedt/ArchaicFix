package org.embeddedt.archaicfix.mixins.client.occlusion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.embeddedt.archaicfix.occlusion.ICulledChunk;
import org.embeddedt.archaicfix.occlusion.IRenderGlobal;
import org.embeddedt.archaicfix.occlusion.OcclusionHelpers;
import org.embeddedt.archaicfix.occlusion.util.IdentityLinkedHashList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = RenderGlobal.class, priority = 900)
public abstract class MixinRenderGlobal implements IRenderGlobal {
    @Shadow private int maxBlockY, minBlockY, maxBlockX, minBlockX, maxBlockZ, minBlockZ;

    @Shadow private int renderChunksWide;

    @Shadow private int renderChunksTall;

    @Shadow private int renderChunksDeep;

    @Shadow private WorldRenderer[] worldRenderers;

    @Shadow private WorldClient theWorld;

    @Shadow private List worldRenderersToUpdate;

    @Shadow private int dummyRenderInt;
    @Shadow private int renderersBeingClipped;
    @Shadow private int renderersBeingOccluded;
    @Shadow private int renderersBeingRendered;
    @Shadow private int renderersSkippingRenderPass;
    @Shadow public int renderersLoaded;
    @Shadow private Minecraft mc;

    @Shadow private int countEntitiesRendered;
    @Shadow private int countEntitiesHidden;
    @Shadow public WorldRenderer[] sortedWorldRenderers;
    @Shadow private int worldRenderersCheckIndex;
    @Shadow private int prevChunkSortX, prevChunkSortY, prevChunkSortZ;

    @Shadow protected abstract void markRenderersForNewPosition(int p_72722_1_, int p_72722_2_, int p_72722_3_);

    @Shadow private double prevRenderSortX, prevRenderSortY, prevRenderSortZ;

    @Shadow private RenderList[] allRenderLists;

    @Shadow public abstract void renderAllRenderLists(int p_72733_1_, double p_72733_2_);

    private Thread clientThread;

    private IdentityLinkedHashList<WorldRenderer> worldRenderersToUpdateList;
    private IdentityLinkedHashList<WorldRenderer> workerWorldRenderers;

    private int prevRenderX, prevRenderY, prevRenderZ;
    private short alphaSortProgress = 0;
    private byte timeCheckInterval = 5, frameCounter, frameTarget;

    private int renderersNeedUpdate;

    @Inject(method = "markBlocksForUpdate", at = @At("HEAD"), cancellable = true)
    private void handleOffthreadUpdate(int x1, int y1, int z1, int x2, int y2, int z2, CallbackInfo ci) {
        ci.cancel();
        if(Thread.currentThread() != clientThread) {
            OcclusionHelpers.updateArea(x1, y1, z1, x2, y2, z2);
        } else
            internalMarkBlockUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void internalMarkBlockUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {

        int xStart = MathHelper.bucketInt(x1, 16);
        int yStart = MathHelper.bucketInt(y1, 16);
        int zStart = MathHelper.bucketInt(z1, 16);
        int xEnd = MathHelper.bucketInt(x2, 16);
        int yEnd = MathHelper.bucketInt(y2, 16);
        int zEnd = MathHelper.bucketInt(z2, 16);

        final int width = this.renderChunksWide;
        final int height = this.renderChunksTall;
        final int depth = this.renderChunksDeep;
        final WorldRenderer[] worldRenderers = this.worldRenderers;
        boolean rebuild = false;

        for (int i = xStart; i <= xEnd; ++i) {
            int x = i % width;
            x += width & (x >> 31);

            for (int j = yStart; j <= yEnd; ++j) {
                int y = j % height;
                y += height & (y >> 31);

                for (int k = zStart; k <= zEnd; ++k) {
                    int z = k % depth;
                    z += depth & (z >> 31);

                    int k4 = (z * height + y) * width + x;
                    WorldRenderer worldrenderer = worldRenderers[k4];

                    if (!worldrenderer.needsUpdate || (worldrenderer.isVisible && !worldRenderersToUpdate.contains(worldrenderer))) {
                        worldrenderer.markDirty();

                        if (worldrenderer.distanceToEntitySquared(mc.renderViewEntity) > 2883.0F) {
                            worldRenderersToUpdate.add(worldrenderer);
                        } else {
                            Chunk chunk = theWorld.getChunkFromBlockCoords(worldrenderer.posX, worldrenderer.posZ);
                            if (((ICulledChunk) chunk).getVisibility()[worldrenderer.posY >> 4].isRenderDirty()) {
                                rebuild = true;
                            }
                            workerWorldRenderers.remove(worldrenderer);
                            workerWorldRenderers.unshift(worldrenderer);
                        }
                    }
                }
            }
        }

        if (rebuild) {
            OcclusionHelpers.worker.dirty = true;
        }
    }

    @Redirect(method = "renderEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RenderManager;renderEntitySimple(Lnet/minecraft/entity/Entity;F)Z"))
    private boolean skipRenderingIfNotVisible(RenderManager instance, Entity entity, float tick) {
        WorldRenderer rend = getRenderer(entity.posX, entity.posY, entity.posZ);
        if (rend != null && !rend.isVisible) {
            --countEntitiesRendered;
            ++countEntitiesHidden;
            return false;
        }
        return RenderManager.instance.renderEntitySimple(entity, tick);
    }

    /**
     * @author skyboy, embeddedt
     * @reason Include information on occlusion
     */
    @Overwrite
    public String getDebugInfoRenders() {
        StringBuilder r = new StringBuilder(3 + 4 + 1 + 4 + 1 + 6 + 5 + 4 + 5 + 4 + 5 + 4 + 5 + 4 + 5 + 3 + 5 + 3 + 5 + 4);
        r.append("C: ").append(renderersBeingRendered).append('/').append(renderersLoaded).append('/').append(worldRenderers.length);
        r.append(". F: ").append(renderersBeingClipped);
        r.append(", O: ").append(renderersBeingOccluded);
        r.append(", E: ").append(renderersSkippingRenderPass);
        r.append(", I: ").append(dummyRenderInt);
        r.append("; U: ").append(renderersNeedUpdate);
        r.append(", W: ").append(workerWorldRenderers.size());
        r.append(", N: ").append(worldRenderersToUpdate.size());
        return r.toString();
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBetterLists(Minecraft p_i1249_1_, CallbackInfo ci) {
        worldRenderersToUpdate = worldRenderersToUpdateList = new IdentityLinkedHashList<>();
        workerWorldRenderers = new IdentityLinkedHashList<>();
        clientThread = Thread.currentThread();
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlCapsChecker;checkARBOcclusion()Z"))
    private boolean neverEnableOcclusion() {
        return false;
    }

    private static int fixPos(int pos, int amt) {
        int r = MathHelper.bucketInt(pos, 16) % amt;
        return r + (amt & (r >> 31));
    }

    @Override
    public WorldRenderer getRenderer(int x, int y, int z) {

        if ((y - 15) > maxBlockY | y < minBlockY)
            return null;
        if ((x - 15) > maxBlockX | x < minBlockX)
            return null;
        if ((z - 15) > maxBlockZ | z < minBlockZ)
            return null;
        x = fixPos(x, renderChunksWide);
        y = fixPos(y, renderChunksTall);
        z = fixPos(z, renderChunksDeep);
        return worldRenderers[(z * renderChunksTall + y) * renderChunksWide + x];
    }

    @Override
    public WorldRenderer getRenderer(double x, double y, double z) {

        int X = MathHelper.floor_double(x);
        int Y = MathHelper.floor_double(y);
        int Z = MathHelper.floor_double(z);
        return getRenderer(X, Y, Z);
    }

    private boolean rebuildChunks(EntityLivingBase view, int lim, long start) {

        IdentityLinkedHashList<WorldRenderer> workerWorldRenderers = this.workerWorldRenderers;
        IdentityLinkedHashList<WorldRenderer> worldRenderersToUpdateList = this.worldRenderersToUpdateList;
        boolean spareTime = true;
        l: for (int c = 0, i = 0; c < lim; ++c) {
            WorldRenderer worldrenderer;
            if (workerWorldRenderers.size() > 0) {
                worldrenderer = workerWorldRenderers.shift();
                worldRenderersToUpdateList.remove(worldrenderer);
            } else {
                worldrenderer = worldRenderersToUpdateList.shift();
            }

            if (worldrenderer == null) {
                break;
            }

            if (!(worldrenderer.isInFrustum & worldrenderer.isVisible)) {
                continue;
            }

            boolean e = worldrenderer.isWaitingOnOcclusionQuery;
            worldrenderer.updateRenderer(view);
            worldrenderer.isVisible &= !e;
            worldrenderer.isWaitingOnOcclusionQuery = worldrenderer.skipAllRenderPasses();
            // can't add fields, re-use

            if (++i > timeCheckInterval) {
                long t = (System.nanoTime() - start) >>> 1;
                if (t > 4500000L >>> 1) {
                    if (i == c | frameCounter == frameTarget) {
                        timeCheckInterval = (byte) (--timeCheckInterval & (~timeCheckInterval));
                        frameTarget = (byte) (frameCounter + 50);
                    }
                    spareTime = false;
                    break l;
                }
                i = 0;
            }
        }
        if (spareTime & frameCounter == frameTarget & timeCheckInterval < 5) {
            ++timeCheckInterval;
            frameTarget = (byte) (frameCounter + 50);
        }
        return spareTime;
    }

    private int prevRotationPitch = -9999;
    private int prevRotationYaw = -9999;

    @Inject(method = "updateRenderers", at = @At("HEAD"), cancellable = true)
    private void performCullingUpdates(EntityLivingBase view, boolean p_72716_2_, CallbackInfoReturnable<Boolean> cir) {
        theWorld.theProfiler.startSection("deferred_updates");
        if (OcclusionHelpers.deferredAreas.size() > 0) {
            long start = System.nanoTime();
            for (int i = 0; OcclusionHelpers.deferredAreas.size() > 0;) {
                OcclusionHelpers.processUpdate(this);

                if (++i > 5) {
                    i = 0;
                    long t = (System.nanoTime() - start) >>> 1;
                    if (t > 200000L >>> 1)
                        break;
                }
            }
        }
        theWorld.theProfiler.endStartSection("rebuild");
        int lim = worldRenderersToUpdate.size() + workerWorldRenderers.size();
        if (lim > 0) {
            ++frameCounter;
            rebuildChunks(view, lim, System.nanoTime());
        }

        theWorld.theProfiler.endStartSection("scan");
        int yaw = MathHelper.floor_float(view.rotationYaw + 45) >> 4;
        int pitch = MathHelper.floor_float(view.rotationPitch + 45) >> 4;
        if (OcclusionHelpers.worker.dirty || yaw != prevRotationYaw || pitch != prevRotationPitch) {
            OcclusionHelpers.worker.run(true);
            prevRotationYaw = yaw;
            prevRotationPitch = pitch;
        }
        theWorld.theProfiler.endSection();
        cir.setReturnValue(true);
    }

    @Inject(method = "setWorldAndLoadRenderers", at = @At("HEAD"))
    private void setWorkerWorld(WorldClient world, CallbackInfo ci) {
        OcclusionHelpers.worker.setWorld((RenderGlobal)(Object)this, world);
    }

    @Inject(method = "loadRenderers", at = @At("HEAD"))
    private void resetLoadedRenderers(CallbackInfo ci) {
        if(theWorld != null) {
            renderersLoaded = 0;
        }
    }

    @Inject(method = "loadRenderers", at = @At("TAIL"))
    private void resetOcclusionWorker(CallbackInfo ci) {
        OcclusionHelpers.worker.dirty = true;
    }

    @Override
    public void pushWorkerRenderer(WorldRenderer wr) {
        workerWorldRenderers.push(wr);
    }

    @Redirect(method = "loadRenderers", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;sort([Ljava/lang/Object;Ljava/util/Comparator;)V", ordinal = 0))
    private void skipSort2(Object[] ts, Comparator<?> comparator) {

    }

    @Redirect(method = "loadRenderers", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;markDirty()V", ordinal = 0))
    private void markRendererInvisible(WorldRenderer instance) {
        instance.isVisible = false;
        instance.isInFrustum = false;
        instance.markDirty();
    }

    @Redirect(method = "markRenderersForNewPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;setPosition(III)V"))
    private void setPositionAndMarkInvisible(WorldRenderer wr, int x, int y, int z) {
        wr.setPosition(x, y, z);
        if(!wr.isInitialized) {
            wr.isWaitingOnOcclusionQuery = false;
            wr.isVisible = false;
        }
    }

    @Inject(method = "markRenderersForNewPosition", at = @At("TAIL"))
    private void runWorker(int p_72722_1_, int p_72722_2_, int p_72722_3_, CallbackInfo ci) {
        OcclusionHelpers.worker.run(true);
    }

    /**
     * @author skyboy, embeddedt
     * @reason Update logic
     */
    @Overwrite
    public int sortAndRender(EntityLivingBase view, int pass, double tick) {
        theWorld.theProfiler.startSection("sortchunks");

        List<WorldRenderer> worldRenderersToUpdate = this.worldRenderersToUpdate;
        WorldRenderer[] sortedWorldRenderers = this.sortedWorldRenderers;
        if (renderersLoaded > 0) {
            int e = renderersLoaded - 10;
            e &= e >> 31;
            e += 10;
            for (int j = 0; j < e; ++j) {
                worldRenderersCheckIndex = (worldRenderersCheckIndex + 1) % renderersLoaded;
                WorldRenderer rend = sortedWorldRenderers[worldRenderersCheckIndex];

                if ((rend.isInFrustum & rend.isVisible) & (rend.needsUpdate || !rend.isInitialized)) {
                    worldRenderersToUpdate.add(rend);
                }
            }
        }

        theWorld.theProfiler.startSection("reposition_chunks");
        if (prevChunkSortX != view.chunkCoordX || prevChunkSortY != view.chunkCoordY || prevChunkSortZ != view.chunkCoordZ) {
            prevChunkSortX = view.chunkCoordX;
            prevChunkSortY = view.chunkCoordY;
            prevChunkSortZ = view.chunkCoordZ;
            markRenderersForNewPosition(MathHelper.floor_double(view.posX), MathHelper.floor_double(view.posY), MathHelper.floor_double(view.posZ));
            // no sorting done here, it's now implicit as part of occlusion
        }
        theWorld.theProfiler.endSection();

        s: {
            if (pass != 1) {
                break s;
            }
            theWorld.theProfiler.startSection("alpha_sort");
            l: if (prevRenderSortX != view.posX || prevRenderSortY != view.posY || prevRenderSortZ != view.posZ) {
                prevRenderSortX = view.posX;
                prevRenderSortY = view.posY;
                prevRenderSortZ = view.posZ;
                {
                    int x = (int) ((prevRenderSortX - view.chunkCoordX * 16) * 2);
                    int y = (int) ((prevRenderSortY - view.chunkCoordY * 16) * 2);
                    int z = (int) ((prevRenderSortZ - view.chunkCoordZ * 16) * 2);
                    if (prevRenderX == x && prevRenderY == y && prevRenderZ == z) {
                        break l;
                    }
                    prevRenderX = x;
                    prevRenderY = y;
                    prevRenderZ = z;
                }
                alphaSortProgress = 0;
                //double x = view.posX - prevSortX;
                //double y = view.posY - prevSortY;
                //double z = view.posZ - prevSortZ;
                //if ((x * x + y * y + z * z) > 16) {
                //prevSortX = view.posX;
                //prevSortY = view.posY;
                //prevSortZ = view.posZ;
                //} else {
                //limit = 2;
                //}
            }
            int amt = renderersLoaded < 27 ? renderersLoaded : Math.max(renderersLoaded >> 1, 27);
            if (alphaSortProgress < amt) {
                for (int i = 0; i < 10 && alphaSortProgress < amt; ++i) {
                    WorldRenderer r = sortedWorldRenderers[alphaSortProgress++];
                    r.updateRendererSort(view);
                }
            }
            theWorld.theProfiler.endSection();
        }

        theWorld.theProfiler.endStartSection("render");
        RenderHelper.disableStandardItemLighting();
        int k = renderSortedRenderers(0, renderersLoaded, pass, tick);

        theWorld.theProfiler.endSection();
        return k;
    }

    /**
     * @author embeddedt, skyboy
     * @reason occlusion culling
     */
    @Overwrite
    @SuppressWarnings("unchecked")
    protected int renderSortedRenderers(int start, int end, int pass, double tick) {

        EntityLivingBase entitylivingbase = mc.renderViewEntity;
        double xOff = entitylivingbase.lastTickPosX + (entitylivingbase.posX - entitylivingbase.lastTickPosX) * tick;
        double yOff = entitylivingbase.lastTickPosY + (entitylivingbase.posY - entitylivingbase.lastTickPosY) * tick;
        double zOff = entitylivingbase.lastTickPosZ + (entitylivingbase.posZ - entitylivingbase.lastTickPosZ) * tick;

        RenderList[] allRenderLists = this.allRenderLists;
        for (int i = 0; i < allRenderLists.length; ++i) {
            allRenderLists[i].resetList();
        }

        int loopStart = start;
        int loopEnd = end;
        byte dir = 1;

        if (pass == 1) {
            loopStart = end - 1;
            loopEnd = start - 1;
            dir = -1;
        }

        if (pass == 0 && mc.gameSettings.showDebugInfo) {

            mc.theWorld.theProfiler.startSection("debug_info");
            int renderersNotInitialized = 0, renderersBeingClipped = 0, renderersBeingOccluded = 0;
            int renderersBeingRendered = 0, renderersSkippingRenderPass = 0, renderersNeedUpdate = 0;
            WorldRenderer[] worldRenderers = this.worldRenderers;
            for (int i = 0, e = worldRenderers.length; i < e; ++i) {
                WorldRenderer rend = worldRenderers[i];
                if (!rend.isInitialized) {
                    ++renderersNotInitialized;
                } else if (!rend.isInFrustum) {
                    ++renderersBeingClipped;
                } else if (!rend.isVisible) {
                    ++renderersBeingOccluded;
                } else if (rend.isWaitingOnOcclusionQuery) {
                    ++renderersSkippingRenderPass;
                } else {
                    ++renderersBeingRendered;
                }
                if (rend.needsUpdate) {
                    ++renderersNeedUpdate;
                }
            }

            this.dummyRenderInt = renderersNotInitialized;
            this.renderersBeingClipped = renderersBeingClipped;
            this.renderersBeingOccluded = renderersBeingOccluded;
            this.renderersBeingRendered = renderersBeingRendered;
            this.renderersSkippingRenderPass = renderersSkippingRenderPass;
            this.renderersNeedUpdate = renderersNeedUpdate;
            mc.theWorld.theProfiler.endSection();
        }

        mc.theWorld.theProfiler.startSection("setup_lists");
        int glListsRendered = 0, allRenderListsLength = 0;
        WorldRenderer[] sortedWorldRenderers = this.sortedWorldRenderers;
        for (int i = loopStart; i != loopEnd; i += dir) {
            WorldRenderer rend = sortedWorldRenderers[i];

            if (rend.isInFrustum & !rend.skipRenderPass[pass]) {

                int renderListIndex;

                l: {
                    for (int j = 0; j < allRenderListsLength; ++j) {
                        if (allRenderLists[j].rendersChunk(rend.posXMinus, rend.posYMinus, rend.posZMinus)) {
                            renderListIndex = j;
                            break l;
                        }
                    }
                    renderListIndex = allRenderListsLength++;
                    allRenderLists[renderListIndex].setupRenderList(rend.posXMinus, rend.posYMinus, rend.posZMinus, xOff, yOff, zOff);
                }

                allRenderLists[renderListIndex].addGLRenderList(rend.getGLCallListForPass(pass));
                ++glListsRendered;
            }
        }

        mc.theWorld.theProfiler.endStartSection("call_lists");

        {
            int xSort = MathHelper.floor_double(xOff);
            int zSort = MathHelper.floor_double(zOff);
            xSort -= xSort & 1023;
            zSort -= zSort & 1023;
            Arrays.sort(allRenderLists, new RenderDistanceSorter(xSort, zSort));
            this.renderAllRenderLists(pass, tick);
        }
        mc.theWorld.theProfiler.endSection();

        return glListsRendered;
    }
}
