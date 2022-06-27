package org.embeddedt.archaicfix.mixins.common.tc4tweaks;

import com.google.common.collect.Iterators;
import net.glease.tc4tweak.modules.generateItemHash.GenerateItemHash;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.embeddedt.archaicfix.thaumcraft.MappingsHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(GenerateItemHash.class)
public class MixinGenerateItemHash {
    @Redirect(method = "generateItemHash", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;iterator()Ljava/util/Iterator;"), remap = false)
    private static Iterator<List<?>> getIterator(ConcurrentHashMap.KeySetView<List<?>, ?> view, Item item, int meta) {
        return Iterators.concat(MappingsHandler.fastMap.get(item.getUnlocalizedName()).iterator(), MappingsHandler.fastMap.get(Block.getBlockFromItem(item).getUnlocalizedName()).iterator());
    }

    @Redirect(method = "generateItemHash", at = @At(value = "INVOKE", target = "Ljava/util/Arrays;sort([I)V"), remap = false)
    private static void skipSort(int[] arr) {
        /* no-op */
    }
}
