package org.embeddedt.archaicfix.config;

import com.falsepattern.lib.config.ConfigException;
import com.falsepattern.lib.config.SimpleGuiConfig;
import org.embeddedt.archaicfix.Tags;

import net.minecraft.client.gui.GuiScreen;

public class ArchaicGuiConfig extends SimpleGuiConfig {
    public ArchaicGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, ArchaicConfig.class, Tags.MODID, Tags.MODNAME);
    }
}