package org.embeddedt.archaicfix.asm;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TargetedMod {
    CHICKENCHUNKS("ChickenChunks", "ChickenChunks"),
    MRTJPCORE("MrTJPCore", "MrTJPCoreMod"),
    CHUNK_PREGENERATOR("ChunkPregenerator", "chunkpregenerator"),
    THERMALEXPANSION("ThermalExpansion", "ThermalExpansion"),
    THERMALFOUNDATION("ThermalFoundation", "ThermalFoundation"),
    GREGTECH6("GregTech", "gregapi"),
    MATTEROVERDRIVE("MatterOverdrive", "mo"),
    PROJECTE("ProjectE", "ProjectE"),
    TC4TWEAKS("TC4Tweaks", "tc4tweak"),
    FASTCRAFT("FastCraft", null),
    OPTIFINE("OptiFine", null),
    MEKANISM("Mekanism", "Mekanism"),
    BOTANIA("Botania", "Botania"),
    COFHCORE("CoFHCore", "CoFHCore"),
    EXTRAUTILS("ExtraUtilities", "ExtraUtilities"),
    DIVINERPG("DivineRPG", "divinerpg"),
    SHIPSMOD("ShipsMod", "cuchaz.ships"),
    JOURNEYMAP("JourneyMap", "journeymap"),
    AM2("ArsMagica2", "arsmagica2"),
    FOODPLUS("FoodPlus", "FoodPlus"),
    DIVERSITY("Diversity", "diversity"),
    WAYSTONES("Waystones", "waystones"),
    AE2("AppliedEnergistics2", "appliedenergistics2"),
    AOA("AdventOfAscension", "nevermine")
    ;

    @Getter
    private final String modName;
    @Getter
    private final String modId;
}
