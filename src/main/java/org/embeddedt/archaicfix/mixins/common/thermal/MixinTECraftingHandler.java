package org.embeddedt.archaicfix.mixins.common.thermal;

import cofh.thermalexpansion.util.crafting.TECraftingHandler;
import cpw.mods.fml.common.Loader;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TECraftingHandler.class)
public class MixinTECraftingHandler {
    @Inject(method = "loadRecipes", at = @At("HEAD"), cancellable = true, remap = false)
    private static void skipIfGt6(CallbackInfo ci) {
        if(Loader.isModLoaded("gregapi") && Loader.isModLoaded("gregtech")) {
            ArchaicLogger.LOGGER.info("Skipped adding unnecessary pyrotheum recipes");
            ci.cancel();
        }
    }
}
