package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.chunkio.ChunkIOExecutor;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.ducks.ILazyChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkIOExecutor.class)
public class MixinChunkIOExecutor {
    @Inject(method = "dropQueuedChunkLoad", at = @At("HEAD"), cancellable = true, remap = false)
    private static void dropLazyChunkLoad(World world, int x, int z, Runnable runnable, CallbackInfo ci) {
        if(!ArchaicConfig.lazyChunkLoading)
            return;
        IChunkProvider provider = world.getChunkProvider();
        if(provider instanceof ILazyChunkProviderServer && ((ILazyChunkProviderServer)provider).dropLazyChunk(x, z, runnable))
            ci.cancel();
    }
}
