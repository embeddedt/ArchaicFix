package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxisAlignedBB.class)
public class MixinAxisAlignedBB {
    private final double XZ_MARGIN = 0.000001;
    private final double Y_MARGIN = 0.000000001;
}
