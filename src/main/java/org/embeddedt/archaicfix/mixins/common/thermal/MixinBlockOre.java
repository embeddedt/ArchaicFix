package org.embeddedt.archaicfix.mixins.common.thermal;

import cofh.thermalfoundation.block.BlockOre;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockOre.class)
public class MixinBlockOre {
    /**
     * @author embeddedt
     * @reason fix crash with invalid meta value
     */
    @Overwrite(remap = false)
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if(meta < BlockOre.LIGHT.length)
            return BlockOre.LIGHT[meta];
        else
            return 0;
    }
}
