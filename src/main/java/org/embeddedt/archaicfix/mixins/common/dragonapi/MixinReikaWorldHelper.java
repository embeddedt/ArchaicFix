package org.embeddedt.archaicfix.mixins.common.dragonapi;

import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.WeakHashMap;

@Mixin(value=ReikaWorldHelper.class, remap=false)
public class MixinReikaWorldHelper {

    private static WeakHashMap<World, Boolean> arch$isWorldFake;

    @Inject(method = "isFakeWorld", at = @At("HEAD"), cancellable = true)
    private static void cacheIsFakeWorldPre(World world, CallbackInfoReturnable<Boolean> cir) {
        if(arch$isWorldFake == null) {
            arch$isWorldFake = new WeakHashMap<>();
        }
        Boolean isFake = arch$isWorldFake.get(world);
        if(isFake != null) {
            cir.setReturnValue(isFake);
        }
    }

    @ModifyReturnValue(method = "isFakeWorld", at = @At("RETURN"))
    private static boolean cacheIsFakeWorldPost(boolean value, World world) {
        arch$isWorldFake.put(world, value);
        return value;
    }

}
