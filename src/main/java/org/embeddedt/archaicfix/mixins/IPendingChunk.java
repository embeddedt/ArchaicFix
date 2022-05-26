package org.embeddedt.archaicfix.mixins;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;

public interface IPendingChunk {
    NBTTagCompound arch$getNbt();
    ChunkCoordIntPair arch$getPos();
}
