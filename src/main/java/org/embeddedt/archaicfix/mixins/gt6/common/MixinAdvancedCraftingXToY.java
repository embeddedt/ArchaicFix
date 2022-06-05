package org.embeddedt.archaicfix.mixins.gt6.common;

import com.google.common.collect.ImmutableSet;
import gregapi.code.ICondition;
import gregapi.oredict.OreDictPrefix;
import gregapi.recipes.AdvancedCraftingXToY;
import net.minecraft.item.Item;
import org.embeddedt.archaicfix.mixins.IAcceleratedRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Modifies AdvancedCraftingXToY to support recipe acceleration, as the matches() call here is quite expensive.
 */
@Mixin(AdvancedCraftingXToY.class)
public class MixinAdvancedCraftingXToY implements IAcceleratedRecipe {
    @Shadow(remap = false) @Final public OreDictPrefix mInput;

    private Set<Item> allPotentialItems;

    @Inject(method = "<init>(Lgregapi/oredict/OreDictPrefix;ILgregapi/oredict/OreDictPrefix;IZLgregapi/code/ICondition;)V", at = @At("RETURN"), remap = false)
    private void setupRecipeCache(OreDictPrefix aInput, int aInputCount, OreDictPrefix aOutput, int aOutputCount, boolean aAutoCraftable, ICondition aCondition, CallbackInfo ci) {
        allPotentialItems = ImmutableSet.copyOf(mInput.mRegisteredItems.stream().map(container -> container.mItem).collect(Collectors.toSet()));
    }
    @Override
    public Set<Item> getPotentialItems() {
        return allPotentialItems;
    }
}
