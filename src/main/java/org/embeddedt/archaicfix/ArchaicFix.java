package org.embeddedt.archaicfix;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = ArchaicFix.MODID, version = ArchaicFix.VERSION)
public class ArchaicFix
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "archaicfix";
    public static final String VERSION = "1.0";

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDebugUpdateQueue());
    }

}
