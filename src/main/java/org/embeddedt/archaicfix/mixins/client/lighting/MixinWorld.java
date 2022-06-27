package org.embeddedt.archaicfix.mixins.lighting.client;

import net.minecraft.world.World;
import org.embeddedt.archaicfix.lighting.world.lighting.LightingEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class MixinWorld {
    private LightingEngine lightingEngine;

    @Inject(method = "finishSetup", at = @At("RETURN"), remap = false)
    private void onConstructed(CallbackInfo ci) {
        this.lightingEngine = new LightingEngine((World) (Object) this);
    }
}
