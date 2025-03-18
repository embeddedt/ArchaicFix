package org.embeddedt.archaicfix.thaumcraft;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.common.registry.GameData;
import net.glease.tc4tweak.modules.generateItemHash.GenerateItemHash;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.embeddedt.archaicfix.ArchaicLogger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.client.gui.GuiResearchRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MappingsHandler {
    public static HashMultimap<String, List<?>> fastMap = null;
    /*
    public static class FastObjectTag {
        public final String originalUnlocalizedName;
        public final Block lookedUpBlock;
        public final Item lookedUpItem;
        public final int[] range;
        public final List<?> list;
        public FastObjectTag(List<?> list) {
            Item item = (Item)list.get(0);
            this.list = list;
            originalUnlocalizedName = item.getUnlocalizedName();
            lookedUpItem = (Item)Item.itemRegistry.getObject(originalUnlocalizedName);
            lookedUpBlock = (Block)Block.blockRegistry.getObject(originalUnlocalizedName)
            if(list.get(1) instanceof int[]) {
                int[] range = (int[])list.get(1);
                this.range = Arrays.copyOf(range, range.length);
                Arrays.sort(this.range);
            } else
                range = null;
        }
    }

     */
    public static void handle() {
        ArchaicLogger.LOGGER.info("Beginning mapping generation");
        List<ItemStack> allStacks = new ArrayList<>();
        for(Object o : GameData.getBlockRegistry()) {
            Block block = (Block)o;
            for(int meta = 0; meta < 16; meta++) {
                allStacks.add(new ItemStack(block, 1, meta));
            }
        }
        for(Object o : GameData.getItemRegistry()) {
            Item item = (Item)o;
            try {
                item.getSubItems(item, item.getCreativeTab(), allStacks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        allStacks.removeIf(stack -> stack == null || stack.getItem() == null);
        ArchaicLogger.LOGGER.info("Collected list of all item stacks - " + allStacks.size());
        HashMultimap<String, List<?>> theMultimap = HashMultimap.create();
        for(List<?> entry : ThaumcraftApi.objectTags.keySet()) {
            if(entry.get(1) instanceof int[]) {
                Arrays.sort((int[])entry.get(1));
            }
            theMultimap.put(((Item)entry.get(0)).getUnlocalizedName(), entry);
        }
        fastMap = theMultimap;
        ArchaicLogger.LOGGER.info("Collected list of object tags - " + GuiResearchRecipe.cache.size() + " cache entries");
        allStacks.parallelStream().forEach(stack -> {
            try {
                GuiResearchRecipe.putToCache(GenerateItemHash.generateItemHash(stack.getItem(), stack.getItemDamage()), stack.copy());
            } catch(Exception ignored) {
            }
        });
        ArchaicLogger.LOGGER.info("Successfully generated item hashes - " + GuiResearchRecipe.cache.size() + " cache entries");
        fastMap = null;
        if(GuiResearchRecipe.cache.size() == 0)
            throw new IllegalStateException("No item hashes at all? How did we get here?");
    }
}
