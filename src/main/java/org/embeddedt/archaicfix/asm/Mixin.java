package org.embeddedt.archaicfix.asm;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import lombok.Getter;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.DragonAPIHelper;

import java.util.Locale;
import java.util.function.Supplier;

@Getter
public enum Mixin implements IMixins {

    MINECRAFT(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .addCommonMixins(
                    "common.core.AccessorEntityLiving",
                    "common.core.MixinEntityPlayerMP",
                    "common.core.MixinWorldServer",
                    "common.core.MixinMapGenStructure",
                    "common.core.MixinEntityVillager",
                    "common.core.MixinMerchantRecipe",
                    "common.core.MixinAxisAlignedBB",
                    "common.core.MixinMaterialLiquid",
                    "common.core.MixinChunkProviderServer",
                    "common.core.MixinChunkIOProvider",
                    "common.core.MixinCraftingManager",
                    "common.core.MixinSpawnerAnimals",
                    "common.core.MixinShapedOreRecipe",
                    "common.core.MixinLongHashMap",
                    "common.core.MixinBlock",
                    "common.core.MixinBlock_Late",
                    "common.core.MixinEnchantmentHelper",
                    "common.core.MixinWorldChunkManager",
                    "common.core.MixinShapedRecipes",
                    "common.core.MixinShapelessOreRecipe",
                    "common.core.MixinShapelessRecipes",
                    "common.core.MixinEntityLiving",
                    "common.core.MixinWorld",
                    "common.core.MixinEntityTrackerEntry",
                    "common.core.MixinEntityXPOrb",
                    "common.core.MixinEntity",
                    "common.core.MixinForgeChunkManager",
                    "common.core.MixinChunk",
                    "common.core.MixinStructureStart",
                    "common.core.MixinOreDictionary",
                    "common.core.MixinChunkProviderHell",
                    "common.core.MixinASMData",
                    "common.core.MixinNetHandlerPlayServer")
            .addClientMixins(
                    "client.core.MixinThreadDownloadImageData",
                    "client.core.MixinBlockFence",
                    "client.core.MixinEntityRenderer",
                    "client.core.MixinGuiBeaconButton",
                    "client.core.MixinGuiButton",
                    "client.core.MixinGuiContainerCreative",
                    "client.core.MixinIntegratedServer",
                    "client.core.MixinChunkProviderClient",
                    "client.core.MixinMinecraft",
                    "client.core.MixinNetHandlerPlayClient",
                    "client.core.MixinGuiCreateWorld",
                    "client.core.MixinGuiIngameForge",
                    "client.core.MixinFMLClientHandler",
                    "client.core.MixinSplashProgress",
                    "client.core.AccessorSplashProgress")),
    PHOSPHOR(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.enablePhosphor)
            .addCommonMixins(
                    "common.lighting.MixinAnvilChunkLoader",
                    "common.lighting.MixinChunk",
                    "common.lighting.MixinChunkProviderServer",
                    "common.lighting.MixinChunkVanilla",
                    "common.lighting.MixinExtendedBlockStorage",
                    "common.lighting.MixinSPacketChunkData",
                    "common.lighting.MixinWorld_Lighting")
            .addClientMixins(
                    "client.lighting.MixinMinecraft",
                    "client.lighting.MixinWorld",
                    "client.lighting.MixinChunkCache")),
    PHOSPHOR_FASTCRAFT(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.enablePhosphor)
            .addRequiredMod(TargetedMod.FASTCRAFT)
            .addCommonMixins(
                    "common.lighting.fastcraft.MixinChunk",
                    "common.lighting.fastcraft.MixinChunkProviderServer",
                    "common.lighting.fastcraft.MixinWorld")),

    GREGTECH6(new MixinBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.GREGTECH6)
            .addCommonMixins(
                    "common.gt6.MixinAdvancedCraftingXToY",
                    "common.gt6.MixinGT6_Main",
                    "common.gt6.MixinCR")
            .addClientMixins("client.gt6.MixinGT_API_Proxy_Client")),

    RACE_CONDITION_LOGGING(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.fixLoginRaceCondition)
            .addCommonMixins(
                    "common.core.MixinNetworkDispatcher",
                    "common.core.MixinNetworkManager",
                    "common.core.MixinEmbeddedChannel")
            .addClientMixins("client.core.MixinNetHandlerLoginClient")),

    MATTER_OVERDRIVE(new MixinBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.MATTER_OVERDRIVE)
            .addCommonMixins(
                    "common.mo.MixinMatterRegistry",
                    "common.mo.MixinMatterRegistrationHandler",
                    "common.mo.MixinVersionCheckHandler")),

    CHUNK_PREGENERATOR(new MixinBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.CHUNK_PREGENERATOR)
            .addCommonMixins(
                    "common.pregen.MixinChunkProcessor",
                    "common.pregen.MixinChunkHelper")),

    THAUMCRAFT_HASHING(new MixinBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.TC4TWEAKS)
            .setApplyIf(() -> ArchaicConfig.betterThaumcraftHashing)
            .addCommonMixins(
                    "common.tc4tweaks.MixinGenerateItemHash",
                    "common.tc4tweaks.MixinMappingThread")),

    MAX_RENDER_DISTANCE(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.raiseMaxRenderDistance)
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addCommonMixins("common.renderdistance.MixinPlayerManager")
            .addClientMixins(
                    "client.renderdistance.MixinGameSettings",
                    "client.renderdistance.MixinRenderGlobal")),

    EXTRA_UTILS(new MixinBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.EXTRAUTILS)
            .addCommonMixins(
                    "common.extrautils.MixinEventHandlerSiege",
                    "common.extrautils.MixinEventHandlerServer",
                    "common.extrautils.MixinItemDivisionSigil",
                    "common.extrautils.MixinTileEntityTrashCan")),

    WORLD_UPDATE_ENTITIES(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.HODGEPODGE)
            .addCommonMixins("common.core.MixinWorld_UpdateEntities")),

    ITEM_LAG_REDUCTION(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.SHIPSMOD)
            .setApplyIf(() -> ArchaicConfig.itemLagReduction)
            .addCommonMixins("common.core.MixinEntityItem")),

    MIXINMODCANDIDATE(new MixinBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.COFHCORE)
            .addCommonMixins("common.core.MixinModCandidate")),

    common_chickenchunks_MixinPlayerChunkViewerManager(Side.COMMON, Phase.EARLY, TargetedMod.CHICKENCHUNKS, "chickenchunks.MixinPlayerChunkViewerManager"),
    common_core_MixinEntityLivingBase_EarlyXpDrop(Side.COMMON, Phase.EARLY, () -> ArchaicConfig.dropXpImmediatelyOnDeath, "core.MixinEntityLivingBase_EarlyXpDrop"),
    common_core_MixinSwampHut(Side.COMMON, Phase.EARLY, () -> ArchaicConfig.fixEntityStructurePersistence, "core.MixinSwampHut"),
    common_mrtjp_MixinBlockUpdateHandler(Side.COMMON, Phase.LATE, TargetedMod.MRTJPCORE, "mrtjp.MixinBlockUpdateHandler"),
    common_projecte_MixinRecipeShapelessHidden(Side.COMMON, Phase.LATE, TargetedMod.PROJECTE, "projecte.MixinRecipeShapelessHidden"),
    common_thermal_MixinTECraftingHandler(Side.COMMON, Phase.LATE, TargetedMod.THERMALEXPANSION, "thermal.MixinTECraftingHandler"),

    // CLIENT MIXINS,
    client_core_MixinSkinManager(Side.CLIENT, Phase.EARLY, () -> ArchaicConfig.fixSkinMemoryLeak, "core.MixinSkinManager"),
    client_core_MixinWorldRenderer(Side.CLIENT, Phase.EARLY, () -> !Boolean.parseBoolean(System.getProperty("archaicFix.disableMC129", "false")), "core.MixinWorldRenderer"),
    client_core_MixinRenderItem(Side.CLIENT, Phase.EARLY, () -> ArchaicConfig.forceFancyItems, "core.MixinRenderItem"),
    client_divinerpg_MixinEntitySparkler(Side.CLIENT, Phase.LATE, TargetedMod.DIVINERPG, "divinerpg.MixinEntitySparkler"),
    client_optifine_MixinVersionCheckThread(Side.CLIENT, Phase.EARLY, TargetedMod.OPTIFINE, () -> ArchaicConfig.disableOFVersionCheck, "optifine.MixinVersionCheckThread"),

    // MOD-FILTERED MIXINS
    common_mekanism_MixinGenHandler(Side.COMMON, Phase.LATE, TargetedMod.MEKANISM, "mekanism.MixinGenHandler"),
    common_thermal_MixinBlockOre(Side.COMMON, Phase.LATE, TargetedMod.THERMALFOUNDATION, "thermal.MixinBlockOre"),
    common_botania_MixinBlockSpecialFlower(Side.COMMON, Phase.LATE, TargetedMod.BOTANIA, "botania.MixinBlockSpecialFlower"),
    client_journeymap_MixinTileDrawStep(Side.CLIENT, Phase.LATE, TargetedMod.JOURNEYMAP, () -> ArchaicConfig.removeJourneymapDebug, "journeymap.MixinTileDrawStep"),
    client_aoa_MixinProjectileEntities(Side.CLIENT, Phase.LATE, TargetedMod.ADVENT_OF_ASCENSION, "aoa.MixinProjectileEntities"),
    common_am2_MixinPlayerTracker(Side.COMMON, Phase.LATE, TargetedMod.ARS_MAGICA_2, "am2.MixinPlayerTracker"),
    common_foodplus_MixinUpdater(Side.COMMON, Phase.LATE, TargetedMod.FOODPLUS, () -> ArchaicConfig.disableFoodPlusUpdates, "foodplus.MixinUpdater"),
    common_waystones_MixinItemWarpStone(Side.COMMON, Phase.LATE, TargetedMod.WAYSTONES, "waystones.MixinItemWarpStone"),
    client_ae2_MixinNEIItemRender(Side.CLIENT, Phase.LATE, TargetedMod.AE2, () -> ArchaicConfig.disableAE2NEIItemRendering, "ae2.MixinNEIItemRender"),
    /** This mixin will ostensibly be unnecessary after DragonAPI V31b */
    common_dragonapi_MixinReikaWorldHelper(Side.COMMON, Phase.LATE, () -> DragonAPIHelper.isVersionInInclusiveRange(0, 'a', 31, 'b') && !Boolean.parseBoolean(System.getProperty("archaicFix.disableFastReikaWorldHelper", "false")), "dragonapi.MixinReikaWorldHelper"),
    common_diversity_MixinServerHandler(Side.COMMON, Phase.LATE, TargetedMod.DIVERSITY, "diversity.MixinServerHandler");

    private final MixinBuilder builder;

    Mixin(MixinBuilder builder) {
        this.builder = builder;
    }

    Mixin(Side side, Phase phase, TargetedMod requiredMod, String mixin) {
        this(side, phase, requiredMod, null, mixin);
    }

    Mixin(Side side, Phase phase, Supplier<Boolean> applyIf, String mixins) {
        this(side, phase, null, applyIf, mixins);
    }

    Mixin(Side side, Phase phase, TargetedMod requiredMod, Supplier<Boolean> applyIf, String mixin) {
        this.builder = new MixinBuilder().setPhase(phase).addSidedMixins(side, getMixinClass(side, mixin));
        if (requiredMod != null) this.builder.addRequiredMod(requiredMod);
        if (applyIf != null) this.builder.setApplyIf(applyIf);
    }

    private static String getMixinClass(Side side, String s) {
        return side.name().toLowerCase(Locale.ROOT) + "." + s;
    }
}
