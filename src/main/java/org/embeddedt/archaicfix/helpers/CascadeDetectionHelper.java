package org.embeddedt.archaicfix.helpers;

import com.google.common.collect.ListMultimap;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.chunk.Chunk;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.config.ArchaicConfig;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;

public class CascadeDetectionHelper {
    private static final ThreadLocal<LinkedList<ChunkCoordIntPair>> arch$populatingChunk = ThreadLocal.withInitial(LinkedList::new);

    private static final LoadController controller = ReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "modController");
    private static final ListMultimap<String, ModContainer> packageOwners = ReflectionHelper.getPrivateValue(LoadController.class, controller, "packageOwners");
    private static final MethodHandle callingStackGetter;

    static {
        try {
            callingStackGetter = MethodHandles.publicLookup().unreflect(ReflectionHelper.findMethod(LoadController.class, controller, new String[] { "getCallingStack" }));
        } catch(ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Class<?>[] getCallingStack() {
        try {
            return (Class<?>[])callingStackGetter.invokeExact();
        } catch(Throwable e) {
            return new Class[0];
        }
    }

    private static ModContainer findModContainer() {
        for(Class<?> clz : getCallingStack()) {
            if(clz.getName().startsWith("net.minecraft") || clz.getName().startsWith("org.embeddedt.archaicfix") || clz.getName().startsWith("cpw.mods.fml")) {
                continue;
            }
            int idx = clz.getName().lastIndexOf('.');
            if(idx == -1) {
                continue;
            }
            String pkg = clz.getName().substring(0, idx);
            List<ModContainer> containers = packageOwners.get(pkg);
            if(containers != null) {
                return containers.get(0);
            }
        }
        return null;
    }

    private static void logCascadingWorldGeneration(Chunk chunk, LinkedList<ChunkCoordIntPair> stack)
    {
        ModContainer activeModContainer = findModContainer();
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
