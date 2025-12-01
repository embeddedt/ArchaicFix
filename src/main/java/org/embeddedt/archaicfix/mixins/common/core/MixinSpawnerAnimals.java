package org.embeddedt.archaicfix.mixins.common.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals {

    @ModifyExpressionValue(method = "findChunksForSpawning", at = @At(value = "CONSTANT", args = "doubleValue=24.0"))
    private double lowerSpawnRange(double old) {
        return ArchaicConfig.fixMobSpawnsAtLowRenderDist ? 16 : old;
    }

    @Redirect(method = "performWorldGenSpawning", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntityInWorld(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean checkForCollision(World world, Entity instance) {
        if (!ArchaicConfig.preventEntitySuffocationWorldgen || world.getCollidingBoundingBoxes(instance, instance.boundingBox).isEmpty()) {
            return world.spawnEntityInWorld(instance);
        }
        return false;
    }
}
