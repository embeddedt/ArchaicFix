package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.SpawnerAnimals;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SpawnerAnimals.class)
public class MixinSpawnerAnimals {
    @ModifyConstant(method = "findChunksForSpawning", constant = @Constant(doubleValue = 24.0D))
    private double lowerSpawnRange(double old) {
        return ArchaicConfig.fixMobSpawnsAtLowRenderDist ? 16 : old;
    }
}
