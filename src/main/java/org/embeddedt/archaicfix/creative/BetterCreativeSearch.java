package org.embeddedt.archaicfix.creative;

import codechicken.nei.ItemList;
import codechicken.nei.api.ItemInfo;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.embeddedt.archaicfix.ArchaicFix;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class BetterCreativeSearch {
    public static void handle(String searchText, GuiContainerCreative.ContainerCreative containercreative, int selectedTabIndex) {
        containercreative.itemList.clear();
        CreativeTabs tab = CreativeTabs.creativeTabArray[selectedTabIndex];
        if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch) {
            tab.displayAllReleventItems(containercreative.itemList);
        } else {
            String search = searchText.toLowerCase();
            List<ItemStack> filteredItems;
            if(search.length() > 0) {
                try {
                    filteredItems = ItemList.forkJoinPool.submit(() ->
                            ArchaicFix.initialCreativeItems.parallelStream()
                                    .filter(stack -> {
                                        String s = ItemInfo.getSearchName(stack);
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
            containercreative.itemList.addAll(filteredItems);
        }
        containercreative.scrollTo(0.0F);
    }
}
