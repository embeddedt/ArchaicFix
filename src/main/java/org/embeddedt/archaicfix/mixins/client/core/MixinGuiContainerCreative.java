package org.embeddedt.archaicfix.mixins.client.core;

import cpw.mods.fml.common.Loader;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Container;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.creative.BetterCreativeSearch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative extends InventoryEffectRenderer {
    @Shadow private static int selectedTabIndex;

    @Shadow private GuiTextField searchField;

    @Shadow private float currentScroll;

    private final boolean neiPresent = true;

    public MixinGuiContainerCreative(Container p_i1089_1_) {
        super(p_i1089_1_);
    }

    /**
     * Use the NEI-indexed item list to perform creative searches, for speedup.
     */
    @Inject(method = "updateCreativeSearch", at = @At(value = "HEAD"), cancellable = true)
    private void updateSearchUsingNEI(CallbackInfo ci) {
        if(ArchaicConfig.useNeiForCreativeSearch && Loader.isModLoaded("NotEnoughItems")) {
            ci.cancel();
            this.currentScroll = 0.0F;
            BetterCreativeSearch.handle(searchField.getText(), (GuiContainerCreative.ContainerCreative)this.inventorySlots, selectedTabIndex);
        }

    }
}
