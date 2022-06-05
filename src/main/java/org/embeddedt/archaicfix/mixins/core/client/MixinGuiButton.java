package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionSlider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuiButton.class)
public class MixinGuiButton {
    @Redirect(method = "drawButton", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/gui/GuiButton;field_146123_n:Z", ordinal = 1))
    private boolean isHovered(GuiButton button) {
        if(button instanceof GuiOptionSlider)
            return button.field_146123_n;
        else
            return false;
    }
}
