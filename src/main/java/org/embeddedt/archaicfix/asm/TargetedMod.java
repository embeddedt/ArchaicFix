package org.embeddedt.archaicfix.asm;


import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;
import lombok.Getter;

@Getter
public enum TargetedMod implements ITargetMod {

    ADVENT_OF_ASCENSION("nevermine"),
    AE2("appliedenergistics2"),
    ARS_MAGICA_2("arsmagica2"),
    BOTANIA("Botania"),
    CHICKENCHUNKS("ChickenChunks"),
    CHUNK_PREGENERATOR("chunkpregenerator"),
    COFHCORE("cofh.asm.LoadingPlugin", "CoFHCore"),
    DIVERSITY("diversity"),
    DIVINERPG("divinerpg"),
    EXTRAUTILS("ExtraUtilities"),
    FASTCRAFT("fastcraft.Tweaker", null),
    FOODPLUS("FoodPlus"),
    GREGTECH6("gregapi"),
    HODGEPODGE("com.mitchej123.hodgepodge.core.HodgepodgeCore", "hodgepodge"),
    JOURNEYMAP("journeymap"),
    MATTER_OVERDRIVE("mo"),
    MEKANISM("Mekanism"),
    MRTJPCORE("MrTJPCoreMod"),
    OPTIFINE("optifine.OptiFineForgeTweaker", null),
    PROJECTE("ProjectE"),
    SHIPSMOD("cuchaz.ships.core.CoreModPlugin","cuchaz.ships"),
    TC4TWEAKS("tc4tweak"),
    THERMALEXPANSION("ThermalExpansion"),
    THERMALFOUNDATION("ThermalFoundation"),
    WAYSTONES("waystones");

    private final TargetModBuilder builder;

    TargetedMod(String modId) {
        this(null, modId);
    }

    TargetedMod(String coremodClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coremodClass).setModId(modId);
    }
}
