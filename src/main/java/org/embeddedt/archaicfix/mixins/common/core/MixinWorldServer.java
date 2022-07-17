package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.WorldServer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @ModifyConstant(method = "tickUpdates", constant = @Constant(intValue = 1000), expect = 2, require = 0)
    private int increaseUpdateLimit(int old) {
        return ArchaicConfig.increaseBlockUpdateLimit ? 65000 : old;
    }

    @Inject(method = "func_152379_p", at = @At("RETURN"), cancellable = true)
    private void shortenBlockUpdateDistance(CallbackInfoReturnable<Integer> cir) {
        if(ArchaicConfig.optimizeBlockTickingDistance > 0) {
            cir.setReturnValue(Math.min(cir.getReturnValue(), ArchaicConfig.optimizeBlockTickingDistance));
        }
    }
}
