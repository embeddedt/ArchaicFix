package org.embeddedt.archaicfix.mixins.common.foodplus;

import com.foodplus.core.updater.Updater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Updater.class)
public class MixinUpdater {
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/eventhandler/EventBus;register(Ljava/lang/Object;)V"), index = 0, remap = false)
    private Object getHandler(Object target) {
        return new Object();
    }
}
