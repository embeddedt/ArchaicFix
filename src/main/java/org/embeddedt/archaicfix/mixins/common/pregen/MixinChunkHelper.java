package org.embeddedt.archaicfix.mixins.common.pregen;

import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import pregenerator.impl.processor.generator.ChunkHelper;

@Mixin(ChunkHelper.class)
public class MixinChunkHelper {
    @ModifyConstant(method = "cleanUp", constant = @Constant(intValue = 1000), remap = false)
    private int increaseUpdateLimit(int old) {
        return ArchaicConfig.increaseBlockUpdateLimit ? 65000 : old;
    }
}
