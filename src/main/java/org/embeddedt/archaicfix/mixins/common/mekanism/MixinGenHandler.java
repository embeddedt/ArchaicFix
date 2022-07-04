package org.embeddedt.archaicfix.mixins.common.mekanism;

import mekanism.common.world.GenHandler;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Random;

@Mixin(GenHandler.class)
public class MixinGenHandler {
    @Redirect(method = "generate", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"), slice = @Slice(
            from = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lmekanism/api/MekanismConfig$general;saltPerChunk:I", remap = false),
            to = @At(value = "INVOKE", target = "Lmekanism/common/world/WorldGenSalt;generate(Lnet/minecraft/world/World;Ljava/util/Random;III)Z")
    ))
    private int getOffsetNextInt(Random instance, int i) {
        if(ArchaicConfig.fixMekanismCascadingWorldgen)
            return instance.nextInt(i) + 8;
        else
            return instance.nextInt(i);
    }
}
