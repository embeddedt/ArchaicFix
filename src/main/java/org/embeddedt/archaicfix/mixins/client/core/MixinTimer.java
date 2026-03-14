package org.embeddedt.archaicfix.mixins.client.core;

import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Timer.class)
public class MixinTimer {
    @Shadow
    private double timeSyncAdjustment;

    @Shadow
    private long field_74285_i;

    /**
     * @author embeddedt
     * @reason Disable vanilla's broken mechanism for trying to coordinate two clocks and just use the result of
     * System.nanoTime() as is. This fixes the client sometimes feeling very sluggish when opening a world.
     */
    @Inject(method = "updateTimer", at = @At("HEAD"))
    private void disableTimeSyncAdjustment(CallbackInfo ci) {
        this.timeSyncAdjustment = 1.0D;
        this.field_74285_i = 0L;
    }
}
