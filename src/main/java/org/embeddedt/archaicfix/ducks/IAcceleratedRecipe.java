package org.embeddedt.archaicfix.ducks;

import net.minecraft.item.Item;

import java.util.Set;

public interface IAcceleratedRecipe {
    Set<Item> getPotentialItems();

    void invalidatePotentialItems();
}
