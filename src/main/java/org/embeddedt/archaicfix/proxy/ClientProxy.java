package org.embeddedt.archaicfix.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.BuiltInResourcePack;
import org.embeddedt.archaicfix.helpers.SoundDeviceThread;

import java.lang.management.ManagementFactory;

public class ClientProxy extends CommonProxy {
    SoundDeviceThread soundThread = null;
    public static volatile boolean soundSystemReloadLock = false;
    @Override
    public void preinit() {
        super.preinit();
        Minecraft.memoryReserve = new byte[0];
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        if(ArchaicConfig.modernizeTextures) {
            BuiltInResourcePack.register("vanilla_overrides");
        }
    }

    float lastIntegratedTickTime;
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(FMLCommonHandler.instance().getSide().isClient() && event.phase == TickEvent.Phase.END) {
            IntegratedServer srv = Minecraft.getMinecraft().getIntegratedServer();
            if(srv != null) {
                long currentTickTime = srv.tickTimeArray[srv.getTickCounter() % 100];
                lastIntegratedTickTime = lastIntegratedTickTime * 0.8F + (float)currentTickTime / 1000000.0F * 0.2F;
            } else
                lastIntegratedTickTime = 0;
        }
    }

    private float gameStartTime = -1;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiOpenEvent event) {
        if(!event.isCanceled() && event.gui instanceof GuiMainMenu && gameStartTime == -1) {
            gameStartTime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000f;
            ArchaicLogger.LOGGER.info("The game loaded in " + gameStartTime + " seconds.");
        }
    }

    @SubscribeEvent
    public void onSoundSetup(SoundLoadEvent event) {
        soundSystemReloadLock = false;
        if(soundThread == null) {
            ArchaicLogger.LOGGER.info("Starting sound device thread");
            soundThread = new SoundDeviceThread();
            soundThread.start();
        }
    }

    /* coerce NaN fog values back to 0 (https://bugs.mojang.com/browse/MC-10480) */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if(Float.isNaN(event.red))
            event.red = 0f;
        if(Float.isNaN(event.green))
            event.green = 0f;
        if(Float.isNaN(event.blue))
            event.blue = 0f;
    }
}
