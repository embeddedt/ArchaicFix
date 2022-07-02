package org.embeddedt.archaicfix.mixins.client.renderdistance;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderGlobal;
import org.embeddedt.archaicfix.ArchaicFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.Buffer;
import java.nio.IntBuffer;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GLAllocation;generateDisplayLists(I)I"))
    private int generateDisplayLists(int p_74526_0_) {
        int chunkNum = (ArchaicFix.MAX_RENDER_DISTANCE * 2) + 2;
        return GLAllocation.generateDisplayLists(chunkNum * chunkNum * 16 * 3);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GLAllocation;createDirectIntBuffer(I)Ljava/nio/IntBuffer;"))
    private IntBuffer createOcclusionBuffer(int p_74526_0_) {
        int chunkNum = (ArchaicFix.MAX_RENDER_DISTANCE * 2) + 2;
        return GLAllocation.createDirectIntBuffer(chunkNum * chunkNum * 16);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/nio/IntBuffer;limit(I)Ljava/nio/Buffer;"))
    private Buffer limitOcclusionBuffer(IntBuffer instance, int i) {
        int chunkNum = (ArchaicFix.MAX_RENDER_DISTANCE * 2) + 2;
        return instance.limit(chunkNum * chunkNum * 16);
    }
}
