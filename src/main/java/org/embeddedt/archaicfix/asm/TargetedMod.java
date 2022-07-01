package org.embeddedt.archaicfix.asm;


import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.ITargetedMod.PredicateHelpers.startsWith;

@RequiredArgsConstructor
public enum TargetedMod implements ITargetedMod {
    CHICKENCHUNKS("ChickenChunks", false, startsWith("chickenchunks")),
    MRTJPCORE("MrTJPCore", false, startsWith("mrtjpcore")),
    CHUNK_PREGENERATOR("ChunkPregenerator", false, startsWith("chunk+pregen")),
    THERMALEXPANSION("ThermalExpansion", false, startsWith("thermalexpansion")),
    GREGTECH6("GregTech", false, startsWith("gregtech")),
    MATTEROVERDRIVE("MatterOverdrive", false, startsWith("matteroverdrive")),
    PROJECTE("ProjectE", false, startsWith("projecte")),
    TC4TWEAKS("TC4Tweaks", false, startsWith("thaumcraft4tweaks")),
    FASTCRAFT("FastCraft", false, startsWith("fastcraft")),
    OPTIFINE("OptiFine", false, startsWith("optifine")),
    ;

    @Getter
    private final String modName;
    @Getter
    private final boolean loadInDevelopment;
    @Getter
    private final Predicate<String> condition;
}