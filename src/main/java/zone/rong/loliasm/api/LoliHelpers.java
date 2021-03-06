package zone.rong.loliasm.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.embeddedt.archaicfix.ArchaicLogger;

import java.util.Map;

public class LoliHelpers {
    private static final boolean noClassCache = false;
    private static final boolean weakClassCache = true;
    private static final boolean noResourceCache = false;
    private static final boolean weakResourceCache = true;
    public static void cleanupLaunchClassLoader() {
        try {
            ArchaicLogger.LOGGER.info("Cleaning up LaunchClassLoader");
            if (noClassCache) {
                LoliReflector.resolveFieldSetter(LaunchClassLoader.class, "cachedClasses").invoke(Launch.classLoader, DummyMap.of());
            } else if (weakClassCache) {
                Map<String, Class<?>> oldClassCache = (Map<String, Class<?>>) LoliReflector.resolveFieldGetter(LaunchClassLoader.class, "cachedClasses").invoke(Launch.classLoader);
                Cache<String, Class<?>> newClassCache = CacheBuilder.newBuilder().concurrencyLevel(2).weakValues().build();
                newClassCache.putAll(oldClassCache);
                LoliReflector.resolveFieldSetter(LaunchClassLoader.class, "cachedClasses").invoke(Launch.classLoader, newClassCache.asMap());
            }
            if (noResourceCache) {
                LoliReflector.resolveFieldSetter(LaunchClassLoader.class, "resourceCache").invoke(Launch.classLoader, new ResourceCache());
                LoliReflector.resolveFieldSetter(LaunchClassLoader.class, "negativeResourceCache").invokeExact(Launch.classLoader, DummyMap.asSet());
            } else if (weakResourceCache) {
                Map<String, byte[]> oldResourceCache = (Map<String, byte[]>) LoliReflector.resolveFieldGetter(LaunchClassLoader.class, "resourceCache").invoke(Launch.classLoader);
                Cache<String, byte[]> newResourceCache = CacheBuilder.newBuilder().concurrencyLevel(2).weakValues().build();
                newResourceCache.putAll(oldResourceCache);
                LoliReflector.resolveFieldSetter(LaunchClassLoader.class, "resourceCache").invoke(Launch.classLoader, newResourceCache.asMap());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
