package org.embeddedt.archaicfix.config;

import net.minecraft.client.gui.GuiScreen;

import static org.embeddedt.archaicfix.ArchaicFix.MODID;
import static org.embeddedt.archaicfix.ArchaicFix.MODNAME;

public class ArchaicGuiConfig extends SimpleGuiConfig {
    public ArchaicGuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, ArchaicConfig.class, MODID, MODNAME);
    }
}