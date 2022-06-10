package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.entity.item.EntityXPOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityXPOrb.class)
public class MixinEntityXPOrb {
    @ModifyConstant(method = "<init>(Lnet/minecraft/world/World;DDDI)V", constant = @Constant(floatValue = 0.5f))
    private float useSmallerSizeAlways(float old) {
        return 0.25f;
    }
}
