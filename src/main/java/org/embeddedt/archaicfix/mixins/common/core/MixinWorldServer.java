package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.WorldServer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @ModifyConstant(method = "tickUpdates", constant = @Constant(intValue = 1000), expect = 2, require = 0)
    private int increaseUpdateLimit(int old) {
        return ArchaicConfig.increaseBlockUpdateLimit ? 65000 : old;
    }
}
