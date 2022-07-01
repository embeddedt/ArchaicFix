package org.embeddedt.archaicfix.mixins.client.renderdistance;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderGlobal;
import org.embeddedt.archaicfix.ArchaicFix;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GLAllocation;generateDisplayLists(I)I"))
    private int generateDisplayLists(int p_74526_0_) {
        int chunkNum = (ArchaicFix.MAX_RENDER_DISTANCE * 2) + 2;
        return GLAllocation.generateDisplayLists(chunkNum * chunkNum * 16 * 3);
    }
}
