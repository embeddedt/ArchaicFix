package org.embeddedt.archaicfix.asm;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.IMixinPlugin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

public class ArchaicMixinPlugin implements IMixinPlugin {
    @Getter
    private final Logger logger = IMixinPlugin.createLogger("ArchaicFix");

    @Override
    public ITargetedMod[] getTargetedModEnumValues() {
        return new ITargetedMod[0];
    }

    @Override
    public IMixin[] getMixinEnumValues() {
        return Mixin.values();
    }
}