package org.embeddedt.archaicfix.mixins.common.lighting;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.embeddedt.archaicfix.lighting.api.ILightingEngineProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {
    /**
     * Injects into the head of saveChunk() to forcefully process all pending light updates. Fail-safe.
     *
     * @author Angeline
     */
    @Inject(method = "saveChunk", at = @At("HEAD"))
    private void onConstructed(World world, Chunk chunkIn, CallbackInfo callbackInfo) {
        ((ILightingEngineProvider) world).getLightingEngine().processLightUpdates();
    }
}
