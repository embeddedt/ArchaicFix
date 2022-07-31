package org.embeddedt.archaicfix.mixins.client.core;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.embeddedt.archaicfix.ArchaicFix;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.NEISearchHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative extends InventoryEffectRenderer {
    private static final ForkJoinPool creativeSearchPool = new ForkJoinPool();
    private static final Function<ItemStack, String> VANILLA_SUPPLIER = stack -> String.join("", stack.getTooltip(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().gameSettings.advancedItemTooltips));

    @Shadow private static int selectedTabIndex;

    @Shadow private GuiTextField searchField;

    @Shadow private float currentScroll;

    private int debounceTicks = 0;
    private boolean needSearchUpdate = false;
    private boolean firstSearch = true;

    public MixinGuiContainerCreative(Container p_i1089_1_) {
        super(p_i1089_1_);
    }

    @Inject(method = "updateCreativeSearch", at = @At(value = "HEAD"), cancellable = true)
    private void asyncSearch(CallbackInfo ci) {
        if(ArchaicConfig.asyncCreativeSearch) {
            ci.cancel();
            needSearchUpdate = true;
            if(firstSearch) {
                debounceTicks = 0;
                firstSearch = false;
            } else
                debounceTicks = 5;
        }
    }

    @Inject(method = "updateScreen", at = @At(value = "TAIL"))
    private void performRealSearch(CallbackInfo ci) {
        if(ArchaicConfig.asyncCreativeSearch && needSearchUpdate) {
            String search = this.searchField.getText().toLowerCase();
            if(search.length() == 0)
                debounceTicks = 0;
            debounceTicks--;
            if(debounceTicks <= 0) {
                needSearchUpdate = false;
                GuiContainerCreative.ContainerCreative containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
                CreativeTabs tab = CreativeTabs.creativeTabArray[selectedTabIndex];
                if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch) {
                    tab.displayAllReleventItems(containercreative.itemList);
                } else {
                    List<ItemStack> filteredItems;
                    if(search.length() > 0) {
                        try {
                            Function<ItemStack, String> nameSupplier = ArchaicFix.NEI_INSTALLED ? new NEISearchHelper() : VANILLA_SUPPLIER;
                            filteredItems = creativeSearchPool.submit(() ->
                                    ArchaicFix.initialCreativeItems.parallelStream()
                                            .filter(stack -> {
                                                String s = nameSupplier.apply(stack);
                                                if(s != null)
                                                    return s.contains(search);
                                                else
                                                    return false;
                                            })
                                            .collect(Collectors.toList())
                            ).get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            filteredItems = ImmutableList.of();
                        }
                    } else
                        filteredItems = ArchaicFix.initialCreativeItems;
                    containercreative.itemList.clear();
                    containercreative.itemList.addAll(filteredItems);
                    containercreative.scrollTo(0.0F);
                    this.currentScroll = 0.0F;
                }
            }
        }
    }
}
