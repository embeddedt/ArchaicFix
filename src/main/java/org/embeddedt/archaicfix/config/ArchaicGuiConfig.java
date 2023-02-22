package org.embeddedt.archaicfix.config;

import net.minecraft.client.gui.GuiScreen;
import org.embeddedt.archaicfix.Tags;

public class ArchaicGuiConfig extends SimpleGuiConfig {
    public ArchaicGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, ArchaicConfig.class, Tags.MODID, Tags.MODNAME);
    }
}