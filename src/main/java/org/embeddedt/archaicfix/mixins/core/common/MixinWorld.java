package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public class MixinWorld {
    @Shadow public boolean isRemote;

    @Redirect(method = "getBiomeGenForCoordsBody", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/WorldChunkManager;getBiomeGenAt(II)Lnet/minecraft/world/biome/BiomeGenBase;"))
    private BiomeGenBase skipBiomeGenOnClient(WorldChunkManager manager, int x, int z) {
        if(this.isRemote)
            return BiomeGenBase.ocean;
        else
            return manager.getBiomeGenAt(x, z);
    }
}
