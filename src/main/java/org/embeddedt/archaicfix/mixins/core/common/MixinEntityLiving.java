package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityLiving.class)
public class MixinEntityLiving {
    @ModifyConstant(method = "despawnEntity", constant = @Constant(doubleValue = 16384.0D))
    private double lowerHardRange(double old) {
        return 96 * 96;
    }
}
