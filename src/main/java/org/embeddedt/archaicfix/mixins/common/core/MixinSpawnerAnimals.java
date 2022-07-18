package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.lighting.world.lighting.LightingEngineHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Iterator;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals {
    @Shadow private HashMap eligibleChunksForSpawning;

    @ModifyConstant(method = "findChunksForSpawning", constant = @Constant(doubleValue = 24.0D))
    private double lowerSpawnRange(double old) {
        return ArchaicConfig.fixMobSpawnsAtLowRenderDist ? 16 : old;
    }

    @Redirect(method = "performWorldGenSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean checkForCollision(World world, Entity instance) {
        if(!ArchaicConfig.preventEntitySuffocationWorldgen || world.getCollidingBoundingBoxes(instance, instance.boundingBox).isEmpty()) {
            return world.spawnEntityInWorld(instance);
        }
        return false;
    }

    @Inject(method = "findChunksForSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;getSpawnPoint()Lnet/minecraft/util/ChunkCoordinates;", ordinal = 0))
    private void removeUnloadedChunks(WorldServer p_77192_1_, boolean p_77192_2_, boolean p_77192_3_, boolean p_77192_4_, CallbackInfoReturnable<Integer> cir) {
        if(!ArchaicConfig.lazyChunkLoading)
            return;
        Iterator<ChunkCoordIntPair> eligibleChunks = this.eligibleChunksForSpawning.keySet().iterator();
        IChunkProvider prov = p_77192_1_.getChunkProvider();
        while(eligibleChunks.hasNext()) {
            ChunkCoordIntPair pair = eligibleChunks.next();
            if(!prov.chunkExists(pair.chunkXPos, pair.chunkZPos))
                eligibleChunks.remove();
        }
    }
}
