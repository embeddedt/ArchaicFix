package org.embeddedt.archaicfix;

import com.falsepattern.lib.config.Config;

@Config(modid = Tags.MODID)
public class ArchaicConfig {
    @Config.Comment("Enables the 1.8-style occlusion culling originally developed by CoFHTweaks")
    @Config.DefaultBoolean(false)
    @Config.RequiresMcRestart
    public static boolean enableOcclusionTweaks;
}
