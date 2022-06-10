package org.embeddedt.archaicfix.lighting.api;

import com.falsepattern.lib.compat.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public interface ILightingEngine {
    void scheduleLightUpdate(EnumSkyBlock lightType, BlockPos pos);

    void processLightUpdates();

    void processLightUpdatesForType(EnumSkyBlock lightType);
}
