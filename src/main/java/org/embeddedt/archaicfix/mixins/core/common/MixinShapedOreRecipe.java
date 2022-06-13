package org.embeddedt.archaicfix.mixins.core.common;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.mixins.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

/**
 * Enhances ShapedOreRecipe to support recipe acceleration. This benefits a lot of mods which make use of this class
 * or a subclass.
 */
@Mixin(ShapedOreRecipe.class)
public class MixinShapedOreRecipe implements IAcceleratedRecipe {
    @Shadow(remap = false) private Object[] input;
    private Set<Item> allPossibleInputs = null;
    @Inject(method = "<init>(Lnet/minecraft/item/ItemStack;[Ljava/lang/Object;)V", at = @At("RETURN"), remap = false)
    private void setupMatchCache(ItemStack output, Object[] recipe, CallbackInfo ci) {
        genMatchCache();
    }

    @Inject(method = "<init>(Lnet/minecraft/item/crafting/ShapedRecipes;Ljava/util/Map;)V", at = @At("RETURN"), remap = false)
    private void setupMatchCache(ShapedRecipes old, Map map, CallbackInfo ci) {
        genMatchCache();
    }

    private void genMatchCache() {
        ImmutableSet.Builder<Item> builder = ImmutableSet.builder();
        for(Object o : input) {
            if(o instanceof ItemStack) {
                builder.add(((ItemStack) o).getItem());
            } else if(o instanceof ArrayList) {
                for(ItemStack stack : ((ArrayList<ItemStack>)o)) {
                    builder.add(stack.getItem());
                }
            } else if(o != null) {
                ArchaicLogger.LOGGER.warn("Couldn't optimize input value: " + o);
                return;
            }
        }
        allPossibleInputs = builder.build();
    }

    @Override
    public Set<Item> getPotentialItems() {
        return allPossibleInputs;
    }
}
