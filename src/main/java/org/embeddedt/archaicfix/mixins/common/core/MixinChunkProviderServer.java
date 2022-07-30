package org.embeddedt.archaicfix.mixins.common.core;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.val;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.tuple.Pair;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.ducks.ILazyChunkProviderServer;
import org.embeddedt.archaicfix.helpers.ChunkQueueSorter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer implements ILazyChunkProviderServer {

    @Shadow(remap = false) public abstract Chunk originalLoadChunk(int p_73158_1_, int p_73158_2_);

    @Shadow public WorldServer worldObj;
    private ObjectOpenHashSet<Pair<ChunkCoordIntPair, Runnable>> chunksToLoadSlow = new ObjectOpenHashSet<>();
    private ArrayList<Pair<ChunkCoordIntPair, Runnable>> chunksToLoadSlowQueue = new ArrayList<>();
    private ObjectOpenHashSet<Pair<ChunkCoordIntPair, Runnable>> chunksToLoadDropQueue = new ObjectOpenHashSet<>();

    @Redirect(method = "unloadChunksIfNotNearSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/DimensionManager;shouldLoadSpawn(I)Z", remap = false))
    private boolean neverLoadSpawn(int dim) {
        return !ArchaicConfig.disableSpawnChunks && DimensionManager.shouldLoadSpawn(dim);
    }

    @Redirect(method = "originalLoadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/IChunkProvider;provideChunk(II)Lnet/minecraft/world/chunk/Chunk;", remap = true), remap = false)
    private Chunk populateChunkWithBiomes(IChunkProvider instance, int chunkX, int chunkZ) {
        Chunk chunk = instance.provideChunk(chunkX, chunkZ);
        if(chunk != null) {
            WorldChunkManager manager = chunk.worldObj.getWorldChunkManager();
            for(int z = 0; z < 16; z++) {
                for(int x = 0; x < 16; x++) {
                    chunk.getBiomeGenForWorldCoords(x, z, manager);
                }
            }
        }
        return chunk;
    }

    @Inject(method = "loadChunk(IILjava/lang/Runnable;)Lnet/minecraft/world/chunk/Chunk;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/ChunkProviderServer;originalLoadChunk(II)Lnet/minecraft/world/chunk/Chunk;", remap = false), cancellable = true, remap = false)
    private void queueChunkGeneration(int chunkX, int chunkZ, Runnable runnable, CallbackInfoReturnable<Chunk> cir) {
        if(ArchaicConfig.lazyChunkLoading && runnable != null) {
            cir.setReturnValue(null);
            ChunkCoordIntPair c = new ChunkCoordIntPair(chunkX, chunkZ);
            val pair= Pair.of(c, runnable);
            if(!chunksToLoadSlow.contains(pair)) {
                chunksToLoadSlow.add(pair);
                chunksToLoadSlowQueue.add(pair);
            }
        }
    }

    @Inject(method = "unloadQueuedChunks", at = @At("RETURN"))
    private void loadLazyChunks(CallbackInfoReturnable<Boolean> cir) {
        if(!ArchaicConfig.lazyChunkLoading)
            return;
        Profiler profiler = this.worldObj.theProfiler;
        profiler.startSection("lazychunks");
        if(chunksToLoadDropQueue.size() > 0) {
            profiler.startSection("drop");
            chunksToLoadSlowQueue.removeIf(k -> chunksToLoadDropQueue.contains(k));
            chunksToLoadSlow.removeAll(chunksToLoadDropQueue);
            chunksToLoadDropQueue.clear();
            profiler.endSection();
        }
        if(chunksToLoadSlowQueue.size() > 0) {
            profiler.startSection("sort");
            if(this.worldObj.playerEntities.size() > 0) {
                ChunkCoordIntPair[] playerChunks = new ChunkCoordIntPair[this.worldObj.playerEntities.size()];
                for(int i = 0; i < this.worldObj.playerEntities.size(); i++) {
                    EntityPlayer player = (EntityPlayer)this.worldObj.playerEntities.get(i);
                    playerChunks[i] = new ChunkCoordIntPair(player.chunkCoordX, player.chunkCoordZ);
                }
                chunksToLoadSlowQueue.sort(new ChunkQueueSorter(playerChunks));
            }
            profiler.endStartSection("load");
            int i;
            int amount = Math.min(5, chunksToLoadSlowQueue.size());
            for(i = 0; i < amount; i++) {
                val chunkPair = chunksToLoadSlowQueue.get(i);
                chunksToLoadSlow.remove(chunkPair);
                this.originalLoadChunk(chunkPair.getLeft().chunkXPos, chunkPair.getLeft().chunkZPos);
                chunkPair.getRight().run();
            }
            chunksToLoadSlowQueue.subList(0, i).clear();
            profiler.endSection();
        }
        profiler.endSection();
    }

    @Override
    public boolean dropLazyChunk(int x, int z, Runnable runnable) {
        if(!ArchaicConfig.lazyChunkLoading)
            return false;
        ChunkCoordIntPair c = new ChunkCoordIntPair(x, z);
        val pair= Pair.of(c, runnable);
        if(chunksToLoadSlow.contains(pair)) {
            chunksToLoadDropQueue.add(pair);
            return true;
        }
        return false;
    }
}
