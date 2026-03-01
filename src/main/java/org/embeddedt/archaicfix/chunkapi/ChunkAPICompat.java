package org.embeddedt.archaicfix.chunkapi;

import com.falsepattern.chunk.api.DataRegistry;

public class ChunkAPICompat 
{
    public static void init()
    {
        DataRegistry.registerDataManager(new ChunkLightingDataManager(), 1000);
    }
}
