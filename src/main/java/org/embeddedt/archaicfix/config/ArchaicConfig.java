package org.embeddedt.archaicfix.config;

import com.falsepattern.lib.config.Config;
import org.embeddedt.archaicfix.Tags;

@Config(modid = Tags.MODID)
public class ArchaicConfig {
    @Config.Comment("Enables the 1.8-style occlusion culling originally developed by CoFHTweaks. Not compatible with OptiFine or FastCraft.")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableOcclusionTweaks;

    @Config.DefaultBoolean(true)
    public static boolean hideDownloadingTerrainScreen;

    @Config.Comment("Improve the sorting of chunk updates, provides a slight improvement to how fast the world renders out to its border. Has no effect if enableOcclusionTweaks is on.")
    @Config.DefaultBoolean(true)
    public static boolean improveRenderSortingOrder;

    @Config.Comment("Prevents buttons from showing a yellow text color when hovered, as was done in 1.14+.")
    @Config.DefaultBoolean(false)
    public static boolean enableNewButtonAppearance;

    @Config.Comment("Have NEI take over creative searching. Much more performant, but requires the GTNH fork of NEI to be installed.")
    @Config.DefaultBoolean(true)
    public static boolean useNeiForCreativeSearch;

    @Config.Comment("Cap the integrated server render distance at a minimum of 8 chunks, and adjust despawn ranges so mobs will always despawn properly on low render distances.")
    @Config.DefaultBoolean(true)
    public static boolean fixMobSpawnsAtLowRenderDist;

    @Config.Comment("Replace the regional difficulty calculation with a TMCW-style one that increases with playtime, not time per chunk.")
    @Config.DefaultBoolean(true)
    public static boolean betterRegionalDifficulty;

    @Config.Comment("Allow 65000 block updates to be performed per tick, rather than 1000.")
    @Config.DefaultBoolean(true)
    public static boolean increaseBlockUpdateLimit;

    @Config.Comment("EXPERIMENTAL: Cache matching crafting recipes to avoid needing to scan the whole list each time.")
    @Config.DefaultBoolean(false)
    public static boolean cacheRecipes;

    @Config.Comment("Disable spawn chunks")
    @Config.DefaultBoolean(true)
    public static boolean disableSpawnChunks;

    @Config.Comment("Reduce lag caused by item entities")
    @Config.DefaultBoolean(true)
    @Config.RequiresMcRestart
    public static boolean itemLagReduction;

    @Config.Comment("Increase the amount of armor mobs wear on average. From TMCW.")
    @Config.DefaultBoolean(true)
    public static boolean increaseMobArmor;

    @Config.Comment("Increase the maximum render distance if OptiFine and FastCraft are not installed.")
    @Config.DefaultBoolean(false)
    public static boolean raiseMaxRenderDistance;

    @Config.Comment("What the maximum render distance should be if raiseMaxRenderDistance is enabled.")
    @Config.DefaultInt(32)
    @Config.RangeInt(min = 16, max = 128)
    public static int newMaxRenderDistance;

    @Config.Comment("EXPERIMENTAL: Replace the Thaumcraft hashing implementation. This really hasn't been tested and probably breaks everything.")
    @Config.DefaultBoolean(false)
    public static boolean betterThaumcraftHashing;

    @Config.Comment("Log when cascading worldgen occurs.")
    @Config.DefaultBoolean(true)
    public static boolean logCascadingWorldgen;

    @Config.Comment("Print a stacktrace when cascading worldgen occurs. Use only for development as this will add more lag in game.")
    @Config.DefaultBoolean(false)
    public static boolean logCascadingWorldgenStacktrace;

    @Config.Comment("Fix instances of cascading worldgen in Mekanism.")
    @Config.DefaultBoolean(true)
    public static boolean fixMekanismCascadingWorldgen;

    @Config.Comment("Fix instances of cascading worldgen in vanilla Minecraft. Turn this option off if you require 100% seed parity.")
    @Config.DefaultBoolean(true)
    public static boolean fixVanillaCascadingWorldgen;

    @Config.Comment("Force all mixins to be loaded and the cache to be cleared. This saves RAM, but may reveal bugs in mods' mixin configs. Based on MemoryLeakFix.")
    @Config.DefaultBoolean(false)
    public static boolean clearMixinCache;

    @Config.Comment("Clean up LaunchClassLoader cache.")
    @Config.DefaultBoolean(true)
    public static boolean clearLaunchLoaderCache;

    @Config.Comment("Only show GT6 tooltip data when Shift is pressed.")
    @Config.DefaultBoolean(true)
    public static boolean hideGT6TooltipDataBehindKey;

    @Config.Comment("Fix the 'TickNextTick list out of synch' error.")
    @Config.DefaultBoolean(true)
    public static boolean fixTickListSynchronization;

    @Config.Comment("Make sure entities don't spawn inside blocks that would make them suffocate. Off by default because it might reduce the number of passive entities that spawn during worldgen.")
    @Config.DefaultBoolean(false)
    public static boolean preventEntitySuffocationWorldgen;

    @Config.Comment("Show block registry name and meta value in F3, similar to 1.8+.")
    @Config.DefaultBoolean(false)
    public static boolean showBlockDebugInfo;

    @Config.Comment("Prevent entities outside a certain distance from being ticked. This does not affect tile entities, and is essentially another view distance slider.")
    @Config.DefaultBoolean(false)
    public static boolean optimizeEntityTicking;

    @Config.Comment("Squared distance outside which most entities aren't ticked, default is 64 blocks.")
    @Config.DefaultInt(4096)
    public static int optimizeEntityTickingDistance;

    @Config.Comment("Distance in chunks at which blocks are ticked, the default value of 0 means to use the render distance.")
    @Config.DefaultInt(0)
    public static int optimizeBlockTickingDistance;

    @Config.Comment("List of entities to ignore for entity ticking optimization.")
    @Config.DefaultStringList({ "Wither", "EnderDragon" })
    public static String[] optimizeEntityTickingIgnoreList;
}
