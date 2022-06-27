package org.embeddedt.archaicfix.mixins.common.gt6;

import com.google.common.collect.ImmutableSet;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.AdvancedCraftingXToY;
import net.minecraft.item.Item;
import org.embeddedt.archaicfix.FixHelper;
import org.embeddedt.archaicfix.ducks.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Modifies AdvancedCraftingXToY to support recipe acceleration, as the matches() call here is quite expensive.
 */
@Mixin(AdvancedCraftingXToY.class)
public class MixinAdvancedCraftingXToY implements IAcceleratedRecipe {
    @Shadow(remap = false) @Final public OreDictPrefix mInput;

    private Set<Item> allPotentialItems = null;

    @Override
    public Set<Item> getPotentialItems() {
        if(allPotentialItems == null) {
            allPotentialItems = ImmutableSet.copyOf(mInput.mRegisteredItems.stream().map(container -> container.mItem).collect(Collectors.toSet()));
            FixHelper.recipesHoldingPotentialItems.add(this);
        }
        return allPotentialItems;
    }

    @Override
    public void invalidatePotentialItems() {
        allPotentialItems = null;
    }
}
