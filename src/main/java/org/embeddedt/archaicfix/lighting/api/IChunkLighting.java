package org.embeddedt.archaicfix.lighting.api;

import com.falsepattern.lib.compat.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface IChunkLighting {
    int getCachedLightFor(EnumSkyBlock enumSkyBlock, BlockPos pos);
}
