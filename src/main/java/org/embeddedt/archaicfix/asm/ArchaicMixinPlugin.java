package org.embeddedt.archaicfix.asm;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.IMixinPlugin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import org.apache.logging.log4j.Logger;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.config.ConfigException;
import org.embeddedt.archaicfix.config.ConfigurationManager;

public class ArchaicMixinPlugin implements IMixinPlugin {
    @Getter
    private final Logger logger = IMixinPlugin.createLogger("ArchaicFix");

    public ArchaicMixinPlugin() {
        try {
            ConfigurationManager.registerConfig(ArchaicConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ITargetedMod[] getTargetedModEnumValues() {
        return TargetedMod.values();
    }

    @Override
    public IMixin[] getMixinEnumValues() {
        return Mixin.values();
    }
}