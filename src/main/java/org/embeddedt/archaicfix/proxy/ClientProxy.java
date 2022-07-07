package org.embeddedt.archaicfix.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.occlusion.OcclusionHelpers;
import zone.rong.loliasm.api.LoliStringPool;

import java.util.ArrayList;

import static org.embeddedt.archaicfix.ArchaicFix.initialCreativeItems;

public class ClientProxy extends CommonProxy {
    @Override
    public void preinit() {
        super.preinit();
        Minecraft.memoryReserve = new byte[0];
        if(ArchaicConfig.enableOcclusionTweaks)
            OcclusionHelpers.init();
        MinecraftForge.EVENT_BUS.register(new LoliStringPool.EventHandler());
    }

    private void fillCreativeItems() {
        if(initialCreativeItems == null) {
            initialCreativeItems = new ArrayList<>();
            for (Object o : Item.itemRegistry) {
                Item item = (Item) o;

                if (item != null && item.getCreativeTab() != null) {
                    item.getSubItems(item, null, initialCreativeItems);
                }
            }
            for(Enchantment enchantment : Enchantment.enchantmentsList) {
                if (enchantment != null && enchantment.type != null)
                {
                    Items.enchanted_book.func_92113_a(enchantment, initialCreativeItems);
                }
            }
        }
    }

    @Override
    public void loadcomplete() {
        super.loadcomplete();
        fillCreativeItems();
    }
}
