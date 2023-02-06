package org.embeddedt.archaicfix.asm;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@RequiredArgsConstructor
public enum TargetedMod {
    CHICKENCHUNKS("ChickenChunks", "ChickenChunks"),
    MRTJPCORE("MrTJPCore", "MrTJPCore"),
    CHUNK_PREGENERATOR("ChunkPregenerator", "chunkpregenerator"),
    THERMALEXPANSION("ThermalExpansion", "ThermalExpansion"),
    THERMALFOUNDATION("ThermalFoundation", "ThermalFoundation"),
    GREGTECH6("GregTech", "gregtech"),
    MATTEROVERDRIVE("MatterOverdrive", "mo"),
    PROJECTE("ProjectE", "ProjectE"),
    TC4TWEAKS("TC4Tweaks", "tc4tweak"),
    FASTCRAFT("FastCraft", null),
    OPTIFINE("OptiFine", null),
    MEKANISM("Mekanism", "Mekanism"),
    BOTANIA("Botania", "Botania"),
    COFHCORE("CoFHCore", "CoFHCore"),
    EXTRAUTILS("ExtraUtilities", "extrautilities"),
    DIVINERPG("DivineRPG", "divinerpg"),
    SHIPSMOD("ShipsMod", "shipsmod"),
    JOURNEYMAP("JourneyMap", "journeymap"),
    AM2("ArsMagica2", "am2"),
    FOODPLUS("FoodPlus", "FoodPlus")
    ;

    @Getter
    private final String modName;
    @Getter
    private final String modId;
}
