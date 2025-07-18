package org.embeddedt.archaicfix.asm;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.DragonAPIHelper;

import javax.annotation.Nonnull;

@Getter
@RequiredArgsConstructor
public enum Mixin implements IMixins {

    MINECRAFT(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .addCommonMixins(
                    "core.AccessorEntityLiving",
                    "core.MixinEntityPlayerMP",
                    "core.MixinWorldServer",
                    "core.MixinMapGenStructure",
                    "core.MixinEntityVillager",
                    "core.MixinMerchantRecipe",
                    "core.MixinAxisAlignedBB",
                    "core.MixinMaterialLiquid",
                    "core.MixinChunkProviderServer",
                    "core.MixinChunkIOProvider",
                    "core.MixinSpawnerAnimals",
                    "core.MixinShapedOreRecipe",
                    "core.MixinLongHashMap",
                    "core.MixinBlock",
                    "core.MixinBlock_Late",
                    "core.MixinEnchantmentHelper",
                    "core.MixinWorldChunkManager",
                    "core.MixinShapedRecipes",
                    "core.MixinShapelessOreRecipe",
                    "core.MixinShapelessRecipes",
                    "core.MixinEntityLiving",
                    "core.MixinWorld",
                    "core.MixinEntityTrackerEntry",
                    "core.MixinEntityXPOrb",
                    "core.MixinEntity",
                    "core.MixinForgeChunkManager",
                    "core.MixinChunk",
                    "core.MixinStructureStart",
                    "core.MixinOreDictionary",
                    "core.MixinChunkProviderHell",
                    "core.MixinASMData",
                    "core.MixinNetHandlerPlayServer")
            .addClientMixins(
                    "core.MixinThreadDownloadImageData",
                    "core.MixinBlockFence",
                    "core.MixinEntityRenderer",
                    "core.MixinGuiBeaconButton",
                    "core.MixinGuiButton",
                    "core.MixinGuiContainerCreative",
                    "core.MixinIntegratedServer",
                    "core.MixinChunkProviderClient",
                    "core.MixinMinecraft",
                    "core.MixinNetHandlerPlayClient",
                    "core.MixinGuiCreateWorld",
                    "core.MixinGuiIngameForge",
                    "core.MixinFMLClientHandler",
                    "core.MixinSplashProgress",
                    "core.AccessorSplashProgress")),
    RECIPE_CACHING(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.cacheRecipes)
            .addCommonMixins("core.MixinCraftingManager")),
    PHOSPHOR(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.enablePhosphor)
            .addCommonMixins(
                    "lighting.MixinAnvilChunkLoader",
                    "lighting.MixinChunk",
                    "lighting.MixinChunkProviderServer",
                    "lighting.MixinChunkVanilla",
                    "lighting.MixinExtendedBlockStorage",
                    "lighting.MixinSPacketChunkData",
                    "lighting.MixinWorld_Lighting")
            .addClientMixins(
                    "lighting.MixinMinecraft",
                    "lighting.MixinWorld",
                    "lighting.MixinChunkCache")),
    PHOSPHOR_FASTCRAFT(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.enablePhosphor)
            .addRequiredMod(TargetedMod.FASTCRAFT)
            .addCommonMixins(
                    "lighting.fastcraft.MixinChunk",
                    "lighting.fastcraft.MixinChunkProviderServer",
                    "lighting.fastcraft.MixinWorld")),

    GREGTECH6(new ArchaicBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.GREGTECH6)
            .addCommonMixins(
                    "gt6.MixinAdvancedCraftingXToY",
                    "gt6.MixinGT6_Main",
                    "gt6.MixinCR")
            .addClientMixins("gt6.MixinGT_API_Proxy_Client")),

    RACE_CONDITION_LOGGING(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.fixLoginRaceCondition)
            .addCommonMixins(
                    "core.MixinNetworkDispatcher",
                    "core.MixinNetworkManager",
                    "core.MixinEmbeddedChannel")
            .addClientMixins("core.MixinNetHandlerLoginClient")),

    MATTER_OVERDRIVE(new ArchaicBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.MATTER_OVERDRIVE)
            .addCommonMixins(
                    "mo.MixinMatterRegistry",
                    "mo.MixinMatterRegistrationHandler",
                    "mo.MixinVersionCheckHandler")),

    CHUNK_PREGENERATOR(new ArchaicBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.CHUNK_PREGENERATOR)
            .addCommonMixins(
                    "pregen.MixinChunkProcessor",
                    "pregen.MixinChunkHelper")),

    THAUMCRAFT_HASHING(new ArchaicBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.TC4TWEAKS)
            .setApplyIf(() -> ArchaicConfig.betterThaumcraftHashing)
            .addCommonMixins(
                    "tc4tweaks.MixinGenerateItemHash",
                    "tc4tweaks.MixinMappingThread")),

    MAX_RENDER_DISTANCE(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.raiseMaxRenderDistance)
            .addExcludedMod(TargetedMod.OPTIFINE)
            .addExcludedMod(TargetedMod.FASTCRAFT)
            .addCommonMixins("renderdistance.MixinPlayerManager")
            .addClientMixins(
                    "renderdistance.MixinGameSettings",
                    "renderdistance.MixinRenderGlobal")),

    EXTRA_UTILS(new ArchaicBuilder()
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.EXTRAUTILS)
            .addCommonMixins(
                    "extrautils.MixinEventHandlerSiege",
                    "extrautils.MixinEventHandlerServer",
                    "extrautils.MixinItemDivisionSigil",
                    "extrautils.MixinTileEntityTrashCan")),

    WORLD_UPDATE_ENTITIES(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.HODGEPODGE)
            .addCommonMixins("core.MixinWorld_UpdateEntities")),

    ITEM_LAG_REDUCTION(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.SHIPSMOD)
            .setApplyIf(() -> ArchaicConfig.itemLagReduction)
            .addCommonMixins("core.MixinEntityItem")),

    MIXINMODCANDIDATE(new ArchaicBuilder()
            .setPhase(Phase.EARLY)
            .addExcludedMod(TargetedMod.COFHCORE)
            .addCommonMixins("core.MixinModCandidate")),

    chickenchunks_MixinPlayerChunkViewerManager(new ArchaicBuilder()
            .addCommonMixins("chickenchunks.MixinPlayerChunkViewerManager")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.CHICKENCHUNKS)),
    core_MixinEntityLivingBase_EarlyXpDrop(new ArchaicBuilder()
            .addCommonMixins("core.MixinEntityLivingBase_EarlyXpDrop")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.dropXpImmediatelyOnDeath)),
    core_MixinSwampHut(new ArchaicBuilder()
            .addCommonMixins("core.MixinSwampHut")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.fixEntityStructurePersistence)),
    mrtjp_MixinBlockUpdateHandler(new ArchaicBuilder()
            .addCommonMixins("mrtjp.MixinBlockUpdateHandler")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.MRTJPCORE)),
    projecte_MixinRecipeShapelessHidden(new ArchaicBuilder()
            .addCommonMixins("projecte.MixinRecipeShapelessHidden")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.PROJECTE)),
    thermal_MixinTECraftingHandler(new ArchaicBuilder()
            .addCommonMixins("thermal.MixinTECraftingHandler")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.THERMALEXPANSION)),

    // CLIENT MIXINS,
    core_MixinSkinManager(new ArchaicBuilder()
            .addClientMixins("core.MixinSkinManager")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.fixSkinMemoryLeak)),
    core_MixinWorldRenderer(new ArchaicBuilder()
            .addClientMixins("core.MixinWorldRenderer")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> !Boolean.parseBoolean(System.getProperty("archaicFix.disableMC129", "false")))),
    core_MixinRenderItem(new ArchaicBuilder()
            .addClientMixins("core.MixinRenderItem")
            .setPhase(Phase.EARLY)
            .setApplyIf(() -> ArchaicConfig.forceFancyItems)),
    divinerpg_MixinEntitySparkler(new ArchaicBuilder()
            .addClientMixins("divinerpg.MixinEntitySparkler")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.DIVINERPG)),
    optifine_MixinVersionCheckThread(new ArchaicBuilder()
            .addClientMixins("optifine.MixinVersionCheckThread")
            .setPhase(Phase.EARLY)
            .addRequiredMod(TargetedMod.OPTIFINE)
            .setApplyIf(() -> ArchaicConfig.disableOFVersionCheck)),

    // MOD-FILTERED MIXINS
    mekanism_MixinGenHandler(new ArchaicBuilder()
            .addCommonMixins("mekanism.MixinGenHandler")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.MEKANISM)),
    thermal_MixinBlockOre(new ArchaicBuilder()
            .addCommonMixins("thermal.MixinBlockOre")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.THERMALFOUNDATION)),
    botania_MixinBlockSpecialFlower(new ArchaicBuilder()
            .addCommonMixins("botania.MixinBlockSpecialFlower")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.BOTANIA)),
    journeymap_MixinTileDrawStep(new ArchaicBuilder()
            .addClientMixins("journeymap.MixinTileDrawStep")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.JOURNEYMAP)
            .setApplyIf(() -> ArchaicConfig.removeJourneymapDebug)),
    aoa_MixinProjectileEntities(new ArchaicBuilder()
            .addClientMixins("aoa.MixinProjectileEntities")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.ADVENT_OF_ASCENSION)),
    am2_MixinPlayerTracker(new ArchaicBuilder()
            .addCommonMixins("am2.MixinPlayerTracker")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.ARS_MAGICA_2)),
    foodplus_MixinUpdater(new ArchaicBuilder()
            .addCommonMixins("foodplus.MixinUpdater")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.FOODPLUS)
            .setApplyIf(() -> ArchaicConfig.disableFoodPlusUpdates)),
    waystones_MixinItemWarpStone(new ArchaicBuilder()
            .addCommonMixins("waystones.MixinItemWarpStone")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.WAYSTONES)),
    ae2_MixinNEIItemRender(new ArchaicBuilder()
            .addClientMixins("ae2.MixinNEIItemRender")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.AE2)
            .setApplyIf(() -> ArchaicConfig.disableAE2NEIItemRendering)),
    /** This mixin will ostensibly be unnecessary after DragonAPI V31b */
    dragonapi_MixinReikaWorldHelper(new ArchaicBuilder()
            .addCommonMixins("dragonapi.MixinReikaWorldHelper")
            .setPhase(Phase.LATE)
            .setApplyIf(() -> DragonAPIHelper.isVersionInInclusiveRange(0, 'a', 31, 'b') && !Boolean.parseBoolean(System.getProperty("archaicFix.disableFastReikaWorldHelper", "false")))),
    diversity_MixinServerHandler(new ArchaicBuilder()
            .addCommonMixins("diversity.MixinServerHandler")
            .setPhase(Phase.LATE)
            .addRequiredMod(TargetedMod.DIVERSITY));

    private final MixinBuilder builder;

    static class ArchaicBuilder extends MixinBuilder {
        @Override
        public MixinBuilder addCommonMixins(@Nonnull String... mixins) {
            for (int i = 0; i < mixins.length; i++) mixins[i] = "common." + mixins[i];
            return super.addCommonMixins(mixins);
        }

        @Override
        public MixinBuilder addClientMixins(@Nonnull String... mixins) {
            for (int i = 0; i < mixins.length; i++) mixins[i] = "client." + mixins[i];
            return super.addClientMixins(mixins);
        }

        @Override
        public MixinBuilder addSidedMixins(@Nonnull Side side, @Nonnull String... mixins) {
            if (side == Side.COMMON) return this.addCommonMixins(mixins);
            if (side == Side.CLIENT) return this.addClientMixins(mixins);
            return super.addSidedMixins(side, mixins);
        }
    }
}
