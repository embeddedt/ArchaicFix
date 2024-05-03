package org.embeddedt.archaicfix.ducks;

import net.minecraft.world.chunk.IChunkProvider;

public interface ICascadeDetectionChunk {
    void arch$populateWithCascadeDetection(IChunkProvider provider1, IChunkProvider provider2, int x, int z);
}
