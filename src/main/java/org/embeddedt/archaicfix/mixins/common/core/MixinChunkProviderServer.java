package org.embeddedt.archaicfix.mixins.common.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.CascadeDetectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ChunkProviderServer.class)
public abstract class MixinChunkProviderServer {

    @Shadow public WorldServer worldObj;

    @Shadow private Set chunksToUnload;

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

    @WrapOperation(method = "originalLoadChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;populateChunk(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V", remap = true), remap = false)
    private void populate2WithCascade(Chunk chunk, IChunkProvider prov1, IChunkProvider prov2, int x, int z, Operation<Void> operation) {
        CascadeDetectionHelper.arch$populateWithCascadeDetection(chunk, () -> operation.call(chunk, prov1, prov2, x, z));
    }


    //Initially populate it with ridiculous values since if it was populated with 0 or -1 by default it might produce a false negative, and return null in the cache.
    @Unique
    private final long[] archaicFix$chunkPositionsCache = new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};

    @Unique
    private final Chunk[] archaicFix$chunkCache = new Chunk[4];

    // Let's check the HashMap access to see if this is in our cache.
    @WrapOperation(method = "provideChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/LongHashMap;getValueByKey(J)Ljava/lang/Object;"))
    private Object cacheChunkFetch(LongHashMap instance, long l, Operation<Object> original) {
        if(!ArchaicConfig.chunkFetchCache) { // Don't pass null into this check
            return original.call(instance, l);
        }

        int thisX = (int) l;
        int thisZ = (int) (l >> 32);

        for (int i = 0; i < archaicFix$chunkPositionsCache.length; i++) {
            long packedChunkCoords = archaicFix$chunkPositionsCache[i];
            int checkX = (int) packedChunkCoords;
            int checkZ = (int) (packedChunkCoords >> 32);
            if (checkX == thisX && checkZ == thisZ && archaicFix$chunkCache[i] != null) {
                return archaicFix$chunkCache[i]; //Found chunk in cache! Don't run the provider again!!!
            }
        }
        Chunk chunk = (Chunk) original.call(instance, l);
        archaicFix$updateCaches(l, chunk);
        return chunk;
    }

    /// Invalidated chunks that are to be unloaded
    @Inject(method = "unloadAllChunks", at = @At("HEAD"))
    private void invalidateCache(CallbackInfo ci) {
        if(ArchaicConfig.chunkFetchCache) {
            archaicFix$resetCaches();
        }
    }

    /// Invalidated chunks that are to be unloaded
    @Inject(method = "unloadQueuedChunks", at = @At("HEAD"))
    private void invalidateChunksToBeUnloaded(CallbackInfoReturnable<Boolean> cir) {
        if(ArchaicConfig.chunkFetchCache) {
            for(long unloadedChunk : (Set<Long>) chunksToUnload) {
                int indexOfChunk = ArrayUtils.indexOf(archaicFix$chunkPositionsCache, unloadedChunk);
                if(indexOfChunk > -1) {
                    archaicFix$chunkCache[indexOfChunk] = null;
                    archaicFix$chunkPositionsCache[indexOfChunk] = Long.MAX_VALUE;
                }
            }
        }
    }

    /// Push the previous highest value out of the caches and add the new ones at the top.
    @Unique
    private void archaicFix$updateCaches(long coords, Chunk chunk) {
        archaicFix$chunkCache[3] = archaicFix$chunkCache[2];
        archaicFix$chunkCache[2] = archaicFix$chunkCache[1];
        archaicFix$chunkCache[1] = archaicFix$chunkCache[0];
        archaicFix$chunkCache[0] = chunk;

        archaicFix$chunkPositionsCache[3] = archaicFix$chunkPositionsCache[2];
        archaicFix$chunkPositionsCache[2] = archaicFix$chunkPositionsCache[1];
        archaicFix$chunkPositionsCache[1] = archaicFix$chunkPositionsCache[0];
        archaicFix$chunkPositionsCache[0] = coords;
    }

    @Unique
    private void archaicFix$resetCaches() {
        for (int i = 3; i > -1; i--) {
            archaicFix$chunkCache[i] = null;
            archaicFix$chunkPositionsCache[i] = Long.MAX_VALUE;
        }
    }
}
