package org.embeddedt.archaicfix.helpers;

import com.falsepattern.lib.dependencies.DependencyLoader;
import com.falsepattern.lib.dependencies.SemanticVersion;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.Tags;

import java.util.concurrent.atomic.AtomicBoolean;

public class FastutilHelper {
    private static final AtomicBoolean fastutilLoaded = new AtomicBoolean(false);
    public static void load() {
        if (!fastutilLoaded.get()) {
            try {
                DependencyLoader.addMavenRepo("https://repo.maven.apache.org/maven2/");
                DependencyLoader.builder()
                        .loadingModId(Tags.MODID)
                        .groupId("it.unimi.dsi")
                        .artifactId("fastutil")
                        .minVersion(new SemanticVersion(8, 5, 8))
                        .maxVersion(new SemanticVersion(8, Integer.MAX_VALUE, Integer.MAX_VALUE))
                        .preferredVersion(new SemanticVersion(8, 5, 8))
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Could not download fastutil!");
            }
            fastutilLoaded.set(true);
        }
    }
    public static void checkLoad() {
        try {
            Class.forName("it.unimi.dsi.fastutil.objects.ObjectOpenHashSet");
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("fastutil was not loaded correctly");
        }
    }
}
