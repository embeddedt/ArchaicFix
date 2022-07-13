package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals {
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
}
