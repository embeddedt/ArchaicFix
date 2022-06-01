package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.world.gen.ChunkProviderServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkProviderServer.class)
public class MixinChunkProviderServer {
    @Redirect(method = "unloadChunksIfNotNearSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/DimensionManager;shouldLoadSpawn(I)Z", remap = false))
    private boolean neverLoadSpawn(int dim) {
        return false;
    }
}
