package org.embeddedt.archaicfix.mixins.lighting.common;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.embeddedt.archaicfix.lighting.api.ILightingEngineProvider;
import org.embeddedt.archaicfix.lighting.world.lighting.LightingEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld implements ILightingEngineProvider {
    private LightingEngine lightingEngine;

    /**
     * @author Angeline
     * Initialize the lighting engine on world construction.
     */
    @Redirect(method = "<init>(Lnet/minecraft/world/storage/ISaveHandler;Ljava/lang/String;Lnet/minecraft/world/WorldSettings;Lnet/minecraft/world/WorldProvider;Lnet/minecraft/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/ISaveHandler;loadWorldInfo()Lnet/minecraft/world/storage/WorldInfo;"))
    private WorldInfo onConstructed(ISaveHandler handler) {
        this.lightingEngine = new LightingEngine((World) (Object) this);
        return handler.loadWorldInfo();
    }


    /**
     * Directs the light update to the lighting engine and always returns a success value.
     * @author Angeline
     */
    @Inject(method = "updateLightByType", at = @At("HEAD"), cancellable = true)
    private void checkLightFor(EnumSkyBlock type, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        this.lightingEngine.scheduleLightUpdate(type, x, y, z);

        cir.setReturnValue(true);
    }

    @Override
    public LightingEngine getLightingEngine() {
        return this.lightingEngine;
    }

    // === FASTCRAFT OVERRIDES ===
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lfastcraft/H;d(Lnet/minecraft/world/World;III)Z", remap = false))
    private boolean updateLightUsingPhosphor(World world, int x, int y, int z) {
        return world.func_147451_t(x, y, z);
    }
}
