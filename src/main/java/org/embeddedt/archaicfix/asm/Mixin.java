package org.embeddedt.archaicfix.asm;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;
import lombok.Getter;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.DragonAPIHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@Getter
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
                    "core.MixinCraftingManager",
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

    chickenchunks_MixinPlayerChunkViewerManager(Side.COMMON, Phase.LATE, TargetedMod.CHICKENCHUNKS, "chickenchunks.MixinPlayerChunkViewerManager"),
    core_MixinEntityLivingBase_EarlyXpDrop(Side.COMMON, Phase.EARLY, () -> ArchaicConfig.dropXpImmediatelyOnDeath, "core.MixinEntityLivingBase_EarlyXpDrop"),
    core_MixinSwampHut(Side.COMMON, Phase.EARLY, () -> ArchaicConfig.fixEntityStructurePersistence, "core.MixinSwampHut"),
    mrtjp_MixinBlockUpdateHandler(Side.COMMON, Phase.LATE, TargetedMod.MRTJPCORE, "mrtjp.MixinBlockUpdateHandler"),
    projecte_MixinRecipeShapelessHidden(Side.COMMON, Phase.LATE, TargetedMod.PROJECTE, "projecte.MixinRecipeShapelessHidden"),
    thermal_MixinTECraftingHandler(Side.COMMON, Phase.LATE, TargetedMod.THERMALEXPANSION, "thermal.MixinTECraftingHandler"),

    // CLIENT MIXINS,
    core_MixinSkinManager(Side.CLIENT, Phase.EARLY, () -> ArchaicConfig.fixSkinMemoryLeak, "core.MixinSkinManager"),
    core_MixinWorldRenderer(Side.CLIENT, Phase.EARLY, () -> !Boolean.parseBoolean(System.getProperty("archaicFix.disableMC129", "false")), "core.MixinWorldRenderer"),
    core_MixinRenderItem(Side.CLIENT, Phase.EARLY, () -> ArchaicConfig.forceFancyItems, "core.MixinRenderItem"),
    divinerpg_MixinEntitySparkler(Side.CLIENT, Phase.LATE, TargetedMod.DIVINERPG, "divinerpg.MixinEntitySparkler"),
    optifine_MixinVersionCheckThread(Side.CLIENT, Phase.EARLY, TargetedMod.OPTIFINE, () -> ArchaicConfig.disableOFVersionCheck, "optifine.MixinVersionCheckThread"),

    // MOD-FILTERED MIXINS
    mekanism_MixinGenHandler(Side.COMMON, Phase.LATE, TargetedMod.MEKANISM, "mekanism.MixinGenHandler"),
    thermal_MixinBlockOre(Side.COMMON, Phase.LATE, TargetedMod.THERMALFOUNDATION, "thermal.MixinBlockOre"),
    botania_MixinBlockSpecialFlower(Side.COMMON, Phase.LATE, TargetedMod.BOTANIA, "botania.MixinBlockSpecialFlower"),
    journeymap_MixinTileDrawStep(Side.CLIENT, Phase.LATE, TargetedMod.JOURNEYMAP, () -> ArchaicConfig.removeJourneymapDebug, "journeymap.MixinTileDrawStep"),
    aoa_MixinProjectileEntities(Side.CLIENT, Phase.LATE, TargetedMod.ADVENT_OF_ASCENSION, "aoa.MixinProjectileEntities"),
    am2_MixinPlayerTracker(Side.COMMON, Phase.LATE, TargetedMod.ARS_MAGICA_2, "am2.MixinPlayerTracker"),
    foodplus_MixinUpdater(Side.COMMON, Phase.LATE, TargetedMod.FOODPLUS, () -> ArchaicConfig.disableFoodPlusUpdates, "foodplus.MixinUpdater"),
    waystones_MixinItemWarpStone(Side.COMMON, Phase.LATE, TargetedMod.WAYSTONES, "waystones.MixinItemWarpStone"),
    ae2_MixinNEIItemRender(Side.CLIENT, Phase.LATE, TargetedMod.AE2, () -> ArchaicConfig.disableAE2NEIItemRendering, "ae2.MixinNEIItemRender"),
    /** This mixin will ostensibly be unnecessary after DragonAPI V31b */
    dragonapi_MixinReikaWorldHelper(Side.COMMON, Phase.LATE, () -> DragonAPIHelper.isVersionInInclusiveRange(0, 'a', 31, 'b') && !Boolean.parseBoolean(System.getProperty("archaicFix.disableFastReikaWorldHelper", "false")), "dragonapi.MixinReikaWorldHelper"),
    diversity_MixinServerHandler(Side.COMMON, Phase.LATE, TargetedMod.DIVERSITY, "diversity.MixinServerHandler");

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
        this.builder = new ArchaicBuilder().setPhase(phase).addSidedMixins(side, mixin);
        if (requiredMod != null) this.builder.addRequiredMod(requiredMod);
        if (applyIf != null) this.builder.setApplyIf(applyIf);
    }

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
