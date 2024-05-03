package org.embeddedt.archaicfix.mixins.common.core;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.embeddedt.archaicfix.helpers.CascadeDetectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = { "net/minecraftforge/common/chunkio/ChunkIOProvider"})
public class MixinChunkIOProvider {
    @WrapOperation(method = "callStage2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;populateChunk(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V", remap = true), remap = false)
    private void callStage2WithCascade(Chunk chunk, IChunkProvider prov1, IChunkProvider prov2, int x, int z, Operation<Void> operation) {
        CascadeDetectionHelper.arch$populateWithCascadeDetection(chunk, () -> operation.call(chunk, prov1, prov2, x, z));
    }
}
