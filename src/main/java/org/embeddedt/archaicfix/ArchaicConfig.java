package org.embeddedt.archaicfix;

import com.falsepattern.lib.config.Config;

@Config(modid = Tags.MODID)
public class ArchaicConfig {
    @Config.Comment("Enables the 1.8-style occlusion culling originally developed by CoFHTweaks")
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
    @Config.DefaultBoolean(true)
    public static boolean cacheRecipes;
}
