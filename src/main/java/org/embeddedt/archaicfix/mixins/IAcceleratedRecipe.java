package org.embeddedt.archaicfix.mixins;

import net.minecraft.item.Item;

import java.util.Collection;
import java.util.Set;

public interface IAcceleratedRecipe {
    Set<Item> getPotentialItems();

    void invalidatePotentialItems();
}
