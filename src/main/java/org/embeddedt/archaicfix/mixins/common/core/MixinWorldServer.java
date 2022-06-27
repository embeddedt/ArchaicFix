package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @ModifyConstant(method = "tickUpdates", constant = @Constant(intValue = 1000), expect = 2, require = 0)
    private int increaseUpdateLimit(int old) {
        return 65000;
    }
}
