package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

//RPLE replaces getLightValue with an overwrite at priority 1000, so this redirect needs to be delayed for mixin to not crash
@Mixin(value = Block.class, priority = 2000)
public abstract class MixinBlock_Late {
    /**
     * @author embeddedt
     * @reason Avoid calling getBlock
     */
    @Redirect(method = "getLightValue(Lnet/minecraft/world/IBlockAccess;III)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), expect = 0, require = 0)
    public Block getLightValue(IBlockAccess world, int x, int y, int z) {
        return (Block)(Object)this;
    }
}
