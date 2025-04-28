package org.embeddedt.archaicfix.mixins.client.ae2;

import appeng.client.gui.AEBaseGui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEBaseGui.class)
public abstract class MixinNEIItemRender extends GuiContainer {

    protected MixinNEIItemRender(Container container) {
        super(container);
    }

    // disable the duplicate text rendering for stack sizes in the AE2 GUI when NEI compatibility is enabled
    @Inject(method = "setItemRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void setItemRender(RenderItem item, CallbackInfoReturnable<RenderItem> cir) {
        final RenderItem ri = itemRender;
        itemRender = item;
        cir.setReturnValue(ri);
    }
}
