package org.embeddedt.archaicfix;

import codechicken.nei.api.ItemInfo;
import com.google.common.collect.BiMap;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.archaicfix.mixins.IAcceleratedRecipe;
import paulscode.sound.SoundSystemConfig;
import thaumcraft.api.ThaumcraftApi;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Mod(modid = ArchaicFix.MODID, version = ArchaicFix.VERSION)
public class ArchaicFix
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "archaicfix";
    public static final String VERSION = "1.0";

    public static Lock REGION_FILE_LOCK = new ReentrantLock();

    public static List<ItemStack> initialCreativeItems = null;

    public static boolean TRIANGULATOR = false;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new LeftClickEventHandler());
        FixHelper helper = new FixHelper();
        MinecraftForge.EVENT_BUS.register(helper);
        FMLCommonHandler.instance().bus().register(helper);
        Minecraft.memoryReserve = new byte[0];
        //SoundSystemConfig.setNumberNormalChannels(1073741824);
        //SoundSystemConfig.setNumberStreamingChannels(1073741823);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDebugUpdateQueue());
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        try {
            Class.forName("com.falsepattern.triangulator.Triangulator");
            TRIANGULATOR = true;
        } catch (Exception e) {
            TRIANGULATOR = false;
        }
        if(initialCreativeItems == null) {
            initialCreativeItems = new ArrayList<>();
            for (Object o : Item.itemRegistry) {
                Item item = (Item) o;

                if (item != null && item.getCreativeTab() != null) {
                    item.getSubItems(item, null, initialCreativeItems);
                }
            }
            for(Enchantment enchantment : Enchantment.enchantmentsList) {
                if (enchantment != null && enchantment.type != null)
                {
                    Items.enchanted_book.func_92113_a(enchantment, initialCreativeItems);
                }
            }
        }
        HashMap<Class<? extends IRecipe>, Integer> recipeTypeMap = new HashMap<>();
        for(Object o : CraftingManager.getInstance().getRecipeList()) {
            recipeTypeMap.compute(((IRecipe)o).getClass(), (key, oldValue) -> {
                if(oldValue == null)
                    return 1;
                else
                    return oldValue + 1;
            });
        }
        recipeTypeMap.entrySet().stream()
                .sorted(Comparator.comparingInt(pair -> pair.getValue()))
                .forEach(pair -> {
                    String acceleratedSuffix = IAcceleratedRecipe.class.isAssignableFrom(pair.getKey()) ? " (accelerated)" : "";
                    ArchaicFix.LOGGER.info("There are " + pair.getValue() + " recipes of type " + pair.getKey().getName() + acceleratedSuffix);
                });
        int totalRecipes = recipeTypeMap.values().stream().reduce(0, Integer::sum);
        int acceleratedRecipes = recipeTypeMap.entrySet().stream().filter(pair -> IAcceleratedRecipe.class.isAssignableFrom(pair.getKey())).map(Map.Entry::getValue).reduce(0, Integer::sum);
        ArchaicFix.LOGGER.info(acceleratedRecipes + " / " + totalRecipes + " recipes are accelerated!");
        if(!Loader.isModLoaded("Thaumcraft")) {
            boolean thaumcraftGhostApiPresent = false;
            try {
                Class.forName("thaumcraft.api.ThaumcraftApi");
                thaumcraftGhostApiPresent = true;
            } catch(Exception e) {

            }
            if(thaumcraftGhostApiPresent) {
                ArchaicFix.LOGGER.info("Cleared " + ThaumcraftApi.objectTags.size() + " unused Thaumcraft aspects");
                ThaumcraftApi.objectTags.clear();
                ThaumcraftApi.groupedObjectTags.clear();
            }
        }
    }

}
