package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.renderer.RenderBlocks;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(RenderBlocks.class)
public class MixinRenderBlocks {
    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYPN:F", opcode = Opcodes.PUTFIELD, ordinal = 0),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZNNN:F", opcode = Opcodes.PUTFIELD, ordinal = 0)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 1, allow = 8)
    private int incrementYValue0(int y) {
        return y + 1;
    }

    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchYZPP:F", opcode = Opcodes.PUTFIELD, ordinal = 0),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZNPN:F", opcode = Opcodes.PUTFIELD, ordinal = 0)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 1, allow = 8)
    private int decrementYValue1(int y) {
        return y - 1;
    }

    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoBrightnessXZPN:I", opcode = Opcodes.PUTFIELD, ordinal = 0),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZNNN:F", opcode = Opcodes.PUTFIELD, ordinal = 2)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 2, allow = 8)
    private int incrementZValue2(int z) {
        return z + 1;
    }

    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchYZPP:F", opcode = Opcodes.PUTFIELD, ordinal = 1),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZNNP:F", opcode = Opcodes.PUTFIELD, ordinal = 2)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 2, allow = 8)
    private int decrementZValue3(int z) {
        return z - 1;
    }

    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYNP:F", opcode = Opcodes.PUTFIELD, ordinal = 1),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZNNN:F", opcode = Opcodes.PUTFIELD, ordinal = 4)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 0, allow = 8)
    private int incrementXValue4(int x) {
        return x + 1;
    }

    @ModifyArg(method = { "renderStandardBlockWithAmbientOcclusion", "renderStandardBlockWithAmbientOcclusionPartial" }, slice = @Slice(
            from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYPP:F", opcode = Opcodes.PUTFIELD, ordinal = 1),
            to = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderBlocks;aoLightValueScratchXYZPNN:F", opcode = Opcodes.PUTFIELD, ordinal = 4)
    ), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IBlockAccess;getBlock(III)Lnet/minecraft/block/Block;"), index = 0, allow = 8)
    private int decrementXValue5(int x) {
        return x - 1;
    }
}
