package org.embeddedt.archaicfix;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Mod(modid = ArchaicFix.MODID, version = ArchaicFix.VERSION)
public class ArchaicFix
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "archaicfix";
    public static final String VERSION = "1.0";

    public static Lock REGION_FILE_LOCK = new ReentrantLock();

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new LeftClickEventHandler());
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDebugUpdateQueue());
    }

}
