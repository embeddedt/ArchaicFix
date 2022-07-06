package org.embeddedt.archaicfix;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.ducks.IAcceleratedRecipe;
import org.embeddedt.archaicfix.occlusion.OcclusionHelpers;
import org.embeddedt.archaicfix.proxy.CommonProxy;
import org.embeddedt.archaicfix.recipe.IFasterCraftingManager;
import thaumcraft.api.ThaumcraftApi;

import java.util.*;

@Mod(modid = ArchaicFix.MODID, version = ArchaicFix.VERSION, guiFactory = "org.embeddedt.archaicfix.config.ArchaicGuiConfigFactory")
public class ArchaicFix
{
    public static final String MODID = Tags.MODID;
    public static final String VERSION = Tags.VERSION;

    public static List<ItemStack> initialCreativeItems = null;

    public static final int MAX_RENDER_DISTANCE = 32;

    private FixHelper helper;

    @SidedProxy(clientSide = "org.embeddedt.archaicfix.proxy.ClientProxy", serverSide = "org.embeddedt.archaicfix.proxy.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        int nextID = ReflectionHelper.getPrivateValue(Entity.class, null, "field_70152_a", "nextEntityID");
        if(nextID == 0) {
            ReflectionHelper.setPrivateValue(Entity.class, null, 1, "field_70152_a", "nextEntityID");
            ArchaicLogger.LOGGER.info("Fixed MC-111480");
        }
        MinecraftForge.EVENT_BUS.register(new LeftClickEventHandler());
        helper = new FixHelper();
        MinecraftForge.EVENT_BUS.register(helper);
        FMLCommonHandler.instance().bus().register(helper);
        proxy.preinit();

        //SoundSystemConfig.setNumberNormalChannels(1073741824);
        //SoundSystemConfig.setNumberStreamingChannels(1073741823);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDebugUpdateQueue());
    }

    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        ((IFasterCraftingManager)CraftingManager.getInstance()).clearRecipeCache();
        ArchaicLogger.LOGGER.info("Cleared recipe cache");
    }

    private void printRecipeDebug() {
        if(!ArchaicConfig.cacheRecipes)
            return;
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
                    ArchaicLogger.LOGGER.info("There are " + pair.getValue() + " recipes of type " + pair.getKey().getName() + acceleratedSuffix);
                });
        int totalRecipes = recipeTypeMap.values().stream().reduce(0, Integer::sum);
        int acceleratedRecipes = recipeTypeMap.entrySet().stream().filter(pair -> IAcceleratedRecipe.class.isAssignableFrom(pair.getKey())).map(Map.Entry::getValue).reduce(0, Integer::sum);
        ArchaicLogger.LOGGER.info(acceleratedRecipes + " / " + totalRecipes + " recipes are accelerated!");
    }

    private void removeThaumcraftLeak() {
        if(!Loader.isModLoaded("Thaumcraft")) {
            boolean thaumcraftGhostApiPresent = false;
            try {
                Class.forName("thaumcraft.api.ThaumcraftApi");
                thaumcraftGhostApiPresent = true;
            } catch(Exception e) {

            }
            if(thaumcraftGhostApiPresent) {
                ArchaicLogger.LOGGER.info("Cleared " + ThaumcraftApi.objectTags.size() + " unused Thaumcraft aspects");
                ThaumcraftApi.objectTags.clear();
                ThaumcraftApi.groupedObjectTags.clear();
            }
        }
    }



    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.loadcomplete();
        printRecipeDebug();
        removeThaumcraftLeak();
    }
}
