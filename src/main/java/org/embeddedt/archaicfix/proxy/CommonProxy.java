package org.embeddedt.archaicfix.proxy;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import zone.rong.loliasm.api.LoliHelpers;

public class CommonProxy {
    public void preinit() {

    }

    public void loadcomplete() {
        if(ArchaicConfig.clearMixinCache)
            MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
        if(ArchaicConfig.clearLaunchLoaderCache)
            LoliHelpers.cleanupLaunchClassLoader();
    }
}
