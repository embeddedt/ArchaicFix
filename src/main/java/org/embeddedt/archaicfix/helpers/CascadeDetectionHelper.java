package org.embeddedt.archaicfix.helpers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.config.ArchaicConfig;

import java.util.LinkedList;

public class CascadeDetectionHelper {
    private static final ThreadLocal<LinkedList<ChunkCoordIntPair>> arch$populatingChunk = ThreadLocal.withInitial(LinkedList::new);

    private static void logCascadingWorldGeneration(Chunk chunk, LinkedList<ChunkCoordIntPair> stack)
    {
        ModContainer activeModContainer = Loader.instance().activeModContainer();
        String format = "{} loaded a new chunk {} in dimension {} ({}) while populating chunk {}, causing cascading worldgen lag.";

        ChunkCoordIntPair pos = new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition);

        if (activeModContainer == null) {
            ArchaicLogger.LOGGER.warn(format, "Minecraft", pos, chunk.worldObj.provider.dimensionId, chunk.worldObj.provider.getDimensionName(), stack.peek());
        } else {
            ArchaicLogger.LOGGER.warn(format, activeModContainer.getName(), pos, chunk.worldObj.provider.dimensionId, chunk.worldObj.provider.getDimensionName(), stack.peek());
            ArchaicLogger.LOGGER.warn("Please report this to the mod's issue tracker. This log can be disabled in the ArchaicFix config.");
        }

        if(ArchaicConfig.logCascadingWorldgenStacktrace) {
            ArchaicLogger.LOGGER.warn("Stacktrace", new Exception("Cascading world generation"));
        }
    }

    public static void arch$populateWithCascadeDetection(Chunk chunk, Runnable runnable) {
        LinkedList<ChunkCoordIntPair> populationStack = arch$populatingChunk.get();
        if(populationStack.size() > 0 && ArchaicConfig.logCascadingWorldgen) logCascadingWorldGeneration(chunk, populationStack);
        populationStack.push(new ChunkCoordIntPair(chunk.xPosition, chunk.zPosition));
        try {
            runnable.run();
        } finally {
            populationStack.pop();
        }
    }
}
