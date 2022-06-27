package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldChunkManager.class)
public class MixinWorldChunkManager {
    @Shadow private BiomeCache biomeCache;

    @Shadow private GenLayer genBiomes;

    @Shadow private GenLayer biomeIndexLayer;

    @Inject(method = "<init>(Lnet/minecraft/world/World;)V", at = @At("RETURN"))
    private void noCacheOnClient(World world, CallbackInfo ci) {
        if(world.isRemote) {
            /* Make sure the client NEVER uses these */
            this.biomeCache = null;
            this.genBiomes = null;
            this.biomeIndexLayer = null;
        }
    }
}
