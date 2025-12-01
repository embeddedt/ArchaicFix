package org.embeddedt.archaicfix.mixins.client.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.integrated.IntegratedServer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IntegratedServer.class)
public class MixinIntegratedServer {
    /**
     * Force the integrated server to have a minimum view distance of 8, so mob spawning works correctly.
     */
    @ModifyExpressionValue(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/settings/GameSettings;renderDistanceChunks:I"))
    private int getRealRenderDistance(int original) {
        return ArchaicConfig.fixMobSpawnsAtLowRenderDist ? Math.max(original, 8) : original;
    }
}
