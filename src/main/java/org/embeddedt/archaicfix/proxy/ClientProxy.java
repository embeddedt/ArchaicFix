package org.embeddedt.archaicfix.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.SoundDeviceThread;
import org.embeddedt.archaicfix.occlusion.OcclusionHelpers;
import zone.rong.loliasm.api.LoliStringPool;

import java.util.ArrayList;

import static org.embeddedt.archaicfix.ArchaicFix.initialCreativeItems;

public class ClientProxy extends CommonProxy {
    SoundDeviceThread soundThread = null;
    public static volatile boolean soundSystemReloadLock = false;
    @Override
    public void preinit() {
        super.preinit();
        Minecraft.memoryReserve = new byte[0];
        if(ArchaicConfig.enableOcclusionTweaks)
            OcclusionHelpers.init();
        MinecraftForge.EVENT_BUS.register(new LoliStringPool.EventHandler());
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if(!minecraft.gameSettings.showDebugInfo)
            return;
        NetHandlerPlayClient cl = minecraft.getNetHandler();
        if(cl != null) {
            IntegratedServer srv = minecraft.getIntegratedServer();

            if (srv != null) {
                String s = String.format("Integrated server @ %.0f ms ticks", lastIntegratedTickTime);
                event.left.add(1, s);
            }
        }
        if(ArchaicConfig.showBlockDebugInfo && minecraft.objectMouseOver != null && minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if(event.right.get(event.right.size()-1).length() > 0)
                event.right.add("");
            Block block = minecraft.theWorld.getBlock(minecraft.objectMouseOver.blockX, minecraft.objectMouseOver.blockY, minecraft.objectMouseOver.blockZ);
            int meta = minecraft.theWorld.getBlockMetadata(minecraft.objectMouseOver.blockX, minecraft.objectMouseOver.blockY, minecraft.objectMouseOver.blockZ);
            event.right.add(Block.blockRegistry.getNameForObject(block));
            event.right.add("meta: " + meta);
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

    private void fillCreativeItems() {
        if(initialCreativeItems == null) {
            initialCreativeItems = new ArrayList<>();
            for (Object o : Item.itemRegistry) {
                Item item = (Item) o;

                if (item != null && item.getCreativeTab() != null) {
                    try {
                        item.getSubItems(item, null, initialCreativeItems);
                    } catch(Exception e) {
                        ArchaicLogger.LOGGER.error("Item " + item + " threw an error while populating the creative item list!", e);
                    }
                }
            }
            for(Enchantment enchantment : Enchantment.enchantmentsList) {
                if (enchantment != null && enchantment.type != null)
                {
                    Items.enchanted_book.func_92113_a(enchantment, initialCreativeItems);
                }
            }
        }
    }

    @Override
    public void loadcomplete() {
        super.loadcomplete();
        fillCreativeItems();
    }
}
