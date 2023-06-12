package org.embeddedt.archaicfix.mixins.common.diversity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Pseudo
@Mixin(targets = "diversity.proxy.ServerHandler")
public class MixinServerHandler {
    /**
     * @reason Diversity searches blocks downwards until it finds something that's not air. This causes an infinite
     * loop in sky dimensions. Let's lie and say there is bedrock below y=0.
     */
    @Redirect(method = "OnSpawnEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;", remap = true), remap = false)
    public Block pretendBedrockIsBelowTheWorld(World world, int x, int y, int z) {
        if(y < 0) {
            return Blocks.bedrock;
        } else {
            return world.getBlock(x, y, z);
        }
    }
}
