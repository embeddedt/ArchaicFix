package org.embeddedt.archaicfix.mixins.client.occlusion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import org.embeddedt.archaicfix.helpers.WorldRendererDistanceHelper;
import org.embeddedt.archaicfix.occlusion.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = RenderGlobal.class, priority = -2)
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

    @Shadow public int renderDistanceChunks;

    @Shadow public abstract void loadRenderers();

    @Shadow
    private int frustumCheckOffset;

    private Thread clientThread;

    private ArrayList<WorldRenderer> worldRenderersToUpdateList;

    private double prevRenderX, prevRenderY, prevRenderZ;
    private int cameraStaticTime;

    private short alphaSortProgress = 0;
    private byte frameCounter, frameTarget;

    private int renderersNeedUpdate;

    private boolean resortUpdateList;
    
    private IRendererUpdateOrderProvider rendererUpdateOrderProvider;

    /* Make sure other threads can see changes to this */
    private volatile boolean deferNewRenderUpdates;

    /**
     * If the update list is not queued for a full resort (e.g. when the player moves or renderers have their positions
     * changed), uses binary search to add the renderer in the update queue at the appropriate place. Otherwise,
     * the renderer is just added to the end of the list.
     * @param wr renderer to add to the list
     */
    private void addRendererToUpdateQueue(WorldRenderer wr) {
        if(!((IWorldRenderer)wr).arch$isInUpdateList()) {
            ((IWorldRenderer)wr).arch$setInUpdateList(true);
            if(mc.renderViewEntity == null || resortUpdateList) {
                worldRenderersToUpdateList.add(wr);
                resortUpdateList = true;
                return;
            }
            if(worldRenderersToUpdateList.size() > 0) {
                double targetDistance = WorldRendererDistanceHelper.betterDistanceSquared(mc.renderViewEntity, wr);
                int low = 0;
                int high = worldRenderersToUpdateList.size() - 1;
                int finalIndex = -1;
                while(low <= high) {
                    int mid = low + (high - low) / 2;
                    WorldRenderer other = worldRenderersToUpdateList.get(mid);
                    double otherDistance = WorldRendererDistanceHelper.betterDistanceSquared(mc.renderViewEntity, other);
                    if(otherDistance < targetDistance) {
                        low = mid + 1;
                    } else if(otherDistance > targetDistance) {
                        high = mid - 1;
                    } else {
                        finalIndex = mid;
                        break;
                    }
                }
                if(finalIndex == -1)
                    finalIndex = high;
                worldRenderersToUpdateList.add(finalIndex+1, wr);
            } else {
                worldRenderersToUpdateList.add(wr);
            }
        }
    }

    /**
     * Queue a renderer to be updated.
     */
    @Inject(method = "markBlocksForUpdate", at = @At("HEAD"), cancellable = true)
    private void handleOffthreadUpdate(int x1, int y1, int z1, int x2, int y2, int z2, CallbackInfo ci) {
        ci.cancel();
        if(deferNewRenderUpdates || Thread.currentThread() != clientThread) {
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

                    if (!worldrenderer.needsUpdate || (worldrenderer.isVisible && !((IWorldRenderer)worldrenderer).arch$isInUpdateList())) {
                        worldrenderer.markDirty();
                        //if (worldrenderer.distanceToEntitySquared(mc.renderViewEntity) <= 2883.0F) {
                            Chunk chunk = theWorld.getChunkFromBlockCoords(worldrenderer.posX, worldrenderer.posZ);
                            if (((ICulledChunk) chunk).getVisibility()[worldrenderer.posY >> 4].isRenderDirty()) {
                                rebuild = true;
                            }
                        //}
                        addRendererToUpdateQueue(worldrenderer);
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
        r.append(", N: ").append(worldRenderersToUpdate.size());
        return r.toString();
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void initBetterLists(Minecraft p_i1249_1_, CallbackInfo ci) {
        worldRenderersToUpdateList = new ArrayList<>();
        /* Make sure any vanilla code modifying the update queue crashes */
        worldRenderersToUpdate = Collections.unmodifiableList(worldRenderersToUpdateList);
        clientThread = Thread.currentThread();
        rendererUpdateOrderProvider = new DefaultRendererUpdateOrderProvider();
    }

    @Redirect(method = "loadRenderers", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 0))
    private void clearRendererUpdateQueue(List instance) {
        if(instance == worldRenderersToUpdate) {
            for(WorldRenderer wr : worldRenderersToUpdateList) {
                ((IWorldRenderer)wr).arch$setInUpdateList(false);
            }
            worldRenderersToUpdateList.clear();
        } else
            throw new AssertionError("Transformer applied to the wrong List.clear method");
    }

    @Redirect(method = { "loadRenderers", "markRenderersForNewPosition" }, at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean sortAndAddRendererUpdateQueue(List instance, Object renderer) {
        if(instance == worldRenderersToUpdate) {
            addRendererToUpdateQueue((WorldRenderer)renderer);
            return true;
        } else
            throw new AssertionError("Transformer applied to the wrong List.clear method");
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OpenGlCapsChecker;checkARBOcclusion()Z"))
    private boolean neverEnableOcclusion() {
        return false;
    }

    private static int fixPos(int pos, int amt) {
        int r = Math.floorDiv(pos, 16) % amt;
        if(r < 0) {
            r += amt;
        }
        return r;
    }

    @Override
    public WorldRenderer getRenderer(int x, int y, int z) {
        if ((y - 15) > maxBlockY || y < minBlockY || (x - 15) > maxBlockX || x < minBlockX || (z - 15) > maxBlockZ || z < minBlockZ)
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

    private boolean rebuildChunks(EntityLivingBase view, long deadline) {
        int updateLimit = deadline == 0 ? 5 : Integer.MAX_VALUE;
        int updates = 0;

        boolean spareTime = true;
        deferNewRenderUpdates = true;
        rendererUpdateOrderProvider.prepare(worldRenderersToUpdateList);
        for (int c = 0; updates < updateLimit && rendererUpdateOrderProvider.hasNext(worldRenderersToUpdateList); ++c) {
            WorldRenderer worldrenderer = rendererUpdateOrderProvider.next(worldRenderersToUpdateList);
            
            ((IWorldRenderer)worldrenderer).arch$setInUpdateList(false);

            if (!(worldrenderer.isInFrustum & worldrenderer.isVisible) && !OcclusionHelpers.DEBUG_LAZY_CHUNK_UPDATES) {
                continue;
            }

            boolean e = worldrenderer.isWaitingOnOcclusionQuery;
            worldrenderer.updateRenderer(view);
            worldrenderer.isVisible &= !e;
            worldrenderer.isWaitingOnOcclusionQuery = worldrenderer.skipAllRenderPasses() || (mc.theWorld.getChunkFromBlockCoords(worldrenderer.posX, worldrenderer.posZ) instanceof EmptyChunk);
            // can't add fields, re-use

            updates++;

            if(!worldrenderer.isWaitingOnOcclusionQuery || deadline != 0 || OcclusionHelpers.DEBUG_LAZY_CHUNK_UPDATES) {
                long t = System.nanoTime();
                if (t > deadline) {
                    spareTime = false;
                    break;
                }
            }
        }
        rendererUpdateOrderProvider.cleanup(worldRenderersToUpdateList);
        deferNewRenderUpdates = false;
        return spareTime;
    }

    @Inject(method = "updateRenderers", at = @At("HEAD"), cancellable = true)
    private void performCullingUpdates(EntityLivingBase view, boolean p_72716_2_, CallbackInfoReturnable<Boolean> cir) {
        theWorld.theProfiler.startSection("deferred_updates");
        while(OcclusionHelpers.deferredAreas.size() > 0) {
            OcclusionHelpers.processUpdate(this);
        }
        theWorld.theProfiler.endStartSection("rebuild");

        EntityLivingBase viewEntity = mc.renderViewEntity;
        float tick = OcclusionHelpers.partialTickTime;
        double viewX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * tick;
        double viewY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * tick;
        double viewZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * tick;

        boolean cameraMoved = viewX != prevRenderX || viewY != prevRenderY || viewZ != prevRenderZ;

        prevRenderX = viewX;
        prevRenderY = viewY;
        prevRenderZ = viewZ;

        boolean cameraRotated =
                PreviousActiveRenderInfo.objectX != ActiveRenderInfo.objectX ||
                PreviousActiveRenderInfo.objectY != ActiveRenderInfo.objectY ||
                PreviousActiveRenderInfo.objectZ != ActiveRenderInfo.objectZ ||
                PreviousActiveRenderInfo.rotationX != ActiveRenderInfo.rotationX ||
                PreviousActiveRenderInfo.rotationYZ != ActiveRenderInfo.rotationYZ ||
                PreviousActiveRenderInfo.rotationZ != ActiveRenderInfo.rotationZ;

        if(!cameraRotated && !cameraMoved) {
            cameraStaticTime++;
        } else {
            cameraStaticTime = 0;
        }

        /*
         * Under certain scenarios (such as renderer.setPosition being called, or the player moving), renderers will]
         * have their distance from the player change. We address that here by sorting the list.
         */
        if(resortUpdateList) {
            worldRenderersToUpdateList.sort(new BasicDistanceSorter(mc.renderViewEntity));
            resortUpdateList = false;
        }
        if (!worldRenderersToUpdate.isEmpty()) {
            ++frameCounter;
            boolean doUpdateAcceleration = cameraStaticTime > 2 && !OcclusionHelpers.DEBUG_LAZY_CHUNK_UPDATES
                    && !OcclusionHelpers.DEBUG_NO_UPDATE_ACCELERATION;
            /* If the camera is not moving, assume a deadline of 30 FPS. */
            rebuildChunks(view, !doUpdateAcceleration ? OcclusionHelpers.chunkUpdateDeadline
                    : mc.entityRenderer.renderEndNanoTime + (1_000_000_000L / 30L));
        }

        theWorld.theProfiler.endStartSection("scan");
        int yaw = MathHelper.floor_float(view.rotationYaw + 45) >> 4;
        int pitch = MathHelper.floor_float(view.rotationPitch + 45) >> 4;
        if (OcclusionHelpers.worker.dirty || cameraRotated || OcclusionHelpers.DEBUG_ALWAYS_RUN_OCCLUSION) {
            OcclusionHelpers.worker.run(true);
            PreviousActiveRenderInfo.objectX = ActiveRenderInfo.objectX;
            PreviousActiveRenderInfo.objectY = ActiveRenderInfo.objectY;
            PreviousActiveRenderInfo.objectZ = ActiveRenderInfo.objectZ;
            PreviousActiveRenderInfo.rotationX = ActiveRenderInfo.rotationX;
            PreviousActiveRenderInfo.rotationYZ = ActiveRenderInfo.rotationYZ;
            PreviousActiveRenderInfo.rotationZ = ActiveRenderInfo.rotationZ;
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
        OcclusionHelpers.updateRendererNeighbors((RenderGlobal)(Object)this, worldRenderers, renderChunksWide, renderChunksDeep, renderChunksTall);
        OcclusionHelpers.worker.dirty = true;
    }

    @Override
    public void pushWorkerRenderer(WorldRenderer wr) {
        if(!(mc.theWorld.getChunkFromBlockCoords(wr.posX, wr.posZ) instanceof EmptyChunk))
            addRendererToUpdateQueue(wr);
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
        if(((IWorldRenderer)wr).arch$isInUpdateList())
            resortUpdateList = true;
        if(!wr.isInitialized) {
            wr.isWaitingOnOcclusionQuery = false;
            wr.isVisible = false;
        }
    }

    @Inject(method = "markRenderersForNewPosition", at = @At("TAIL"))
    private void runWorker(int p_72722_1_, int p_72722_2_, int p_72722_3_, CallbackInfo ci) {
        OcclusionHelpers.updateRendererNeighbors((RenderGlobal)(Object)this, worldRenderers, renderChunksWide, renderChunksDeep, renderChunksTall);
        OcclusionHelpers.worker.run(true);
    }

    /**
     * @author skyboy, embeddedt
     * @reason Update logic
     */
    @Overwrite
    public int sortAndRender(EntityLivingBase view, int pass, double tick) {
        theWorld.theProfiler.startSection("sortchunks");

        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks && !(this.mc.currentScreen instanceof GuiVideoSettings))
        {
            this.loadRenderers();
        }

        WorldRenderer[] sortedWorldRenderers = this.sortedWorldRenderers;
        if (renderersLoaded > 0) {
            int e = renderersLoaded - 10;
            e &= e >> 31;
            e += 10;
            for (int j = 0; j < e; ++j) {
                worldRenderersCheckIndex = (worldRenderersCheckIndex + 1) % renderersLoaded;
                WorldRenderer rend = sortedWorldRenderers[worldRenderersCheckIndex];

                if ((rend.isInFrustum & rend.isVisible) & (rend.needsUpdate || !rend.isInitialized) & !(this.mc.theWorld.getChunkFromBlockCoords(rend.posX, rend.posZ) instanceof EmptyChunk)) {
                    addRendererToUpdateQueue(rend);
                }
            }
        }

        theWorld.theProfiler.startSection("reposition_chunks");
        if (prevChunkSortX != view.chunkCoordX || prevChunkSortY != view.chunkCoordY || prevChunkSortZ != view.chunkCoordZ) {
            prevChunkSortX = view.chunkCoordX;
            prevChunkSortY = view.chunkCoordY;
            prevChunkSortZ = view.chunkCoordZ;
            markRenderersForNewPosition(MathHelper.floor_double(view.posX), MathHelper.floor_double(view.posY), MathHelper.floor_double(view.posZ));
            resortUpdateList = true;
        }
        theWorld.theProfiler.endSection();

        if(pass == 1){
            theWorld.theProfiler.startSection("alpha_sort");
            if(distanceSquared(view.posX, view.posY, view.posZ, prevRenderSortX, prevRenderSortY, prevRenderSortZ) > 1) {
                prevRenderSortX = view.posX;
                prevRenderSortY = view.posY;
                prevRenderSortZ = view.posZ;

                alphaSortProgress = 0;
            }

            int amt = renderersLoaded < 27 ? renderersLoaded : Math.max(renderersLoaded >> 1, 27);
            if (alphaSortProgress < amt) {
                int amountPerFrame = 1;
                for (int i = 0; i < amountPerFrame && alphaSortProgress < amt; ++i) {
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

            if (rend.isVisible && rend.isInFrustum & !rend.skipRenderPass[pass]) {

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

    /**
     * @author makamys
     * @reason Integrate with the logic in {@link org.embeddedt.archaicfix.occlusion.OcclusionHelpers.RenderWorker#run(boolean)}.
     */
    @Overwrite
    public void clipRenderersByFrustum(ICamera p_72729_1_, float p_72729_2_) {
        for (int i = 0; i < this.worldRenderers.length; ++i) {
            WorldRenderer wr = this.worldRenderers[i];
            IWorldRenderer iwr = (IWorldRenderer)wr;
            if (wr.isInFrustum && (i + this.frustumCheckOffset & 15) == 0 && iwr.arch$isFrustumCheckPending()) {
                wr.updateInFrustum(p_72729_1_);
                iwr.arch$setIsFrustumCheckPending(false);
                if(!wr.isInFrustum) {
                    OcclusionHelpers.worker.dirtyFrustumRenderers++;
                }
            }
        }

        ++this.frustumCheckOffset;

        if(this.frustumCheckOffset % 15 == 0 && OcclusionHelpers.worker.dirtyFrustumRenderers > 0) {
            OcclusionHelpers.worker.dirty = true;
            OcclusionHelpers.worker.dirtyFrustumRenderers = 0;
        }
    }

    @Override
    public void arch$setRendererUpdateOrderProvider(IRendererUpdateOrderProvider orderProvider) {
        this.rendererUpdateOrderProvider = orderProvider;
    }

    private static double distanceSquared(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2);
    }
}
