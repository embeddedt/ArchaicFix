package org.embeddedt.archaicfix.asm;

import com.falsepattern.lib.mixin.IMixin;
import com.falsepattern.lib.mixin.ITargetedMod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.embeddedt.archaicfix.config.ArchaicConfig;

import java.util.*;
import java.util.function.Predicate;

import static com.falsepattern.lib.mixin.IMixin.PredicateHelpers.*;

@RequiredArgsConstructor
public enum Mixin implements IMixin {
    // COMMON MIXINS
    common_chickenchunks_MixinPlayerChunkViewerManager(Side.COMMON, require(TargetedMod.CHICKENCHUNKS), "chickenchunks.MixinPlayerChunkViewerManager"),
    common_core_MixinEntityPlayerMP(Side.COMMON, always(), "core.MixinEntityPlayerMP"),
    common_core_MixinWorldServer(Side.COMMON, always(), "core.MixinWorldServer"),
    common_core_MixinMapGenStructure(Side.COMMON, always(), "core.MixinMapGenStructure"),
    common_core_MixinEntityVillager(Side.COMMON, always(), "core.MixinEntityVillager"),
    common_core_MixinMerchantRecipe(Side.COMMON, always(), "core.MixinMerchantRecipe"),
    common_core_MixinAxisAlignedBB(Side.COMMON, always(), "core.MixinAxisAlignedBB"),
    common_core_MixinMaterialLiquid(Side.COMMON, always(), "core.MixinMaterialLiquid"),
    common_core_MixinChunkProviderServer(Side.COMMON, always(), "core.MixinChunkProviderServer"),
    common_core_MixinCraftingManager(Side.COMMON, always(), "core.MixinCraftingManager"),
    common_core_MixinSpawnerAnimals(Side.COMMON, always(), "core.MixinSpawnerAnimals"),
    common_core_MixinShapedOreRecipe(Side.COMMON, always(), "core.MixinShapedOreRecipe"),
    common_core_MixinLongHashMap(Side.COMMON, always(), "core.MixinLongHashMap"),
    common_core_MixinBlock(Side.COMMON, always(), "core.MixinBlock"),
    common_core_MixinEnchantmentHelper(Side.COMMON, always(), "core.MixinEnchantmentHelper"),
    common_core_MixinWorldChunkManager(Side.COMMON, always(), "core.MixinWorldChunkManager"),
    common_core_MixinShapedRecipes(Side.COMMON, always(), "core.MixinShapedRecipes"),
    common_core_MixinShapelessOreRecipe(Side.COMMON, always(), "core.MixinShapelessOreRecipe"),
    common_core_MixinShapelessRecipes(Side.COMMON, always(), "core.MixinShapelessRecipes"),
    common_core_MixinEntityLiving(Side.COMMON, always(), "core.MixinEntityLiving"),
    common_core_MixinWorld(Side.COMMON, always(), "core.MixinWorld"),
    common_core_MixinEntityTrackerEntry(Side.COMMON, always(), "core.MixinEntityTrackerEntry"),
    common_core_MixinEntityXPOrb(Side.COMMON, always(), "core.MixinEntityXPOrb"),
    common_core_MixinEntityItem(Side.COMMON, m -> ArchaicConfig.itemLagReduction, "core.MixinEntityItem"),
    common_core_MixinEntity(Side.COMMON, always(), "core.MixinEntity"),
    common_core_MixinForgeChunkManager(Side.COMMON, always(), "core.MixinForgeChunkManager"),
    common_core_MixinChunk(Side.COMMON, always(), "core.MixinChunk"),
    common_core_MixinStructureStart(Side.COMMON, always(), "core.MixinStructureStart"),
    common_core_MixinOreDictionary(Side.COMMON, always(), "core.MixinOreDictionary"),
    common_core_MixinChunkProviderHell(Side.COMMON, always(), "core.MixinChunkProviderHell"),
    common_core_MixinASMData(Side.COMMON, always(), "core.MixinASMData"),
    common_core_MixinModCandidate(Side.COMMON, avoid(TargetedMod.COFHCORE), "core.MixinModCandidate"),
    common_core_MixinChunkIOExecutor(Side.COMMON, avoid(TargetedMod.COFHCORE), "core.MixinChunkIOExecutor"),
    common_core_MixinNetworkDispatcher(Side.COMMON, m -> ArchaicConfig.fixLoginRaceCondition, "core.MixinNetworkDispatcher"),
    common_core_MixinNetworkManager(Side.COMMON, m -> ArchaicConfig.fixLoginRaceCondition, "core.MixinNetworkManager"),
    common_core_MixinEmbeddedChannel(Side.COMMON, m -> ArchaicConfig.fixLoginRaceCondition, "core.MixinEmbeddedChannel"),
    common_core_MixinNetHandlerPlayServer(Side.COMMON, always(), "core.MixinNetHandlerPlayServer"),
    common_gt6_MixinAdvancedCraftingXToY(Side.COMMON, require(TargetedMod.GREGTECH6), "gt6.MixinAdvancedCraftingXToY"),
    common_gt6_MixinGT6_Main(Side.COMMON, require(TargetedMod.GREGTECH6), "gt6.MixinGT6_Main"),
    common_gt6_MixinCR(Side.COMMON, require(TargetedMod.GREGTECH6), "gt6.MixinCR"),
    common_lighting_MixinAnvilChunkLoader(Side.COMMON, always(), "lighting.MixinAnvilChunkLoader"),
    common_lighting_MixinChunk(Side.COMMON, always(), "lighting.MixinChunk"),
    common_lighting_MixinChunkProviderServer(Side.COMMON, always(), "lighting.MixinChunkProviderServer"),
    common_lighting_MixinChunkVanilla(Side.COMMON, always(), "lighting.MixinChunkVanilla"),
    common_lighting_MixinExtendedBlockStorage(Side.COMMON, always(), "lighting.MixinExtendedBlockStorage"),
    common_lighting_MixinSPacketChunkData(Side.COMMON, always(), "lighting.MixinSPacketChunkData"),
    common_lighting_MixinWorld(Side.COMMON, always(), "lighting.MixinWorld_Lighting"),
    common_mo_MixinMatterRegistry(Side.COMMON, require(TargetedMod.MATTEROVERDRIVE), "mo.MixinMatterRegistry"),
    common_mo_MixinMatterRegistrationHandler(Side.COMMON, require(TargetedMod.MATTEROVERDRIVE), "mo.MixinMatterRegistrationHandler"),
    common_mo_MixinVersionCheckHandler(Side.COMMON, require(TargetedMod.MATTEROVERDRIVE), "mo.MixinVersionCheckHandler"),
    common_mrtjp_MixinBlockUpdateHandler(Side.COMMON, require(TargetedMod.MRTJPCORE), "mrtjp.MixinBlockUpdateHandler"),
    common_pregen_MixinChunkProcessor(Side.COMMON, require(TargetedMod.CHUNK_PREGENERATOR), "pregen.MixinChunkProcessor"),
    common_pregen_MixinChunkHelper(Side.COMMON, require(TargetedMod.CHUNK_PREGENERATOR), "pregen.MixinChunkHelper"),
    common_projecte_MixinRecipeShapelessHidden(Side.COMMON, require(TargetedMod.PROJECTE), "projecte.MixinRecipeShapelessHidden"),
    common_tc4tweaks_MixinGenerateItemHash(Side.COMMON, require(TargetedMod.TC4TWEAKS).and(m -> ArchaicConfig.betterThaumcraftHashing), "tc4tweaks.MixinGenerateItemHash"),
    common_tc4tweaks_MixinMappingThread(Side.COMMON, require(TargetedMod.TC4TWEAKS).and(m -> ArchaicConfig.betterThaumcraftHashing), "tc4tweaks.MixinMappingThread"),
    common_thermal_MixinTECraftingHandler(Side.COMMON, require(TargetedMod.THERMALEXPANSION), "thermal.MixinTECraftingHandler"),
    // CLIENT MIXINS
    client_core_MixinThreadDownloadImageData(Side.CLIENT, always(), "core.MixinThreadDownloadImageData"),
    client_core_MixinBlockFence(Side.CLIENT, always(), "core.MixinBlockFence"),
    client_core_MixinEntityRenderer(Side.CLIENT, always(), "core.MixinEntityRenderer"),
    client_core_MixinGuiBeaconButton(Side.CLIENT, always(), "core.MixinGuiBeaconButton"),
    client_core_MixinGuiButton(Side.CLIENT, always(), "core.MixinGuiButton"),
    client_core_MixinGuiContainerCreative(Side.CLIENT, always(), "core.MixinGuiContainerCreative"),
    client_core_MixinIntegratedServer(Side.CLIENT, always(), "core.MixinIntegratedServer"),
    client_core_MixinSkinManager(Side.CLIENT, always(), "core.MixinSkinManager"),
    client_core_MixinChunkProviderClient(Side.CLIENT, always(), "core.MixinChunkProviderClient"),
    client_core_MixinWorldRenderer(Side.CLIENT, always(), "core.MixinWorldRenderer"),
    client_core_MixinMinecraft(Side.CLIENT, always(), "core.MixinMinecraft"),
    client_core_MixinNetHandlerPlayClient(Side.CLIENT, always(), "core.MixinNetHandlerPlayClient"),
    client_core_MixinRenderSorter(Side.CLIENT, always(), "core.MixinRenderSorter"),
    client_core_MixinGuiCreateWorld(Side.CLIENT, always(), "core.MixinGuiCreateWorld"),
    client_core_MixinFMLClientHandler(Side.CLIENT, always(), "core.MixinFMLClientHandler"),
    client_core_MixinNetHandlerLoginClient(Side.CLIENT, m -> ArchaicConfig.fixLoginRaceCondition, "core.MixinNetHandlerLoginClient"),
    client_gt6_MixinGT_API_Proxy_Client(Side.CLIENT, require(TargetedMod.GREGTECH6), "gt6.MixinGT_API_Proxy_Client"),
    client_lighting_MixinMinecraft(Side.CLIENT, always(), "lighting.MixinMinecraft"),
    client_lighting_MixinWorld(Side.CLIENT, always(), "lighting.MixinWorld"),
    client_lighting_MixinChunkCache(Side.CLIENT, always(), "lighting.MixinChunkCache"),

    client_optifine_MixinVersionCheckThread(Side.CLIENT, require(TargetedMod.OPTIFINE).and(m -> ArchaicConfig.disableOFVersionCheck), "optifine.MixinVersionCheckThread"),

    client_occlusion_MixinChunk(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.enableOcclusionTweaks), "occlusion.MixinChunk"),
    client_occlusion_MixinEntityRenderer(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.enableOcclusionTweaks), "occlusion.MixinEntityRenderer"),
    client_occlusion_MixinRenderGlobal(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.enableOcclusionTweaks), "occlusion.MixinRenderGlobal"),
    client_occlusion_MixinGuiVideoSettings(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.enableOcclusionTweaks), "occlusion.MixinGuiVideoSettings"),
    client_occlusion_MixinWorldRenderer(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.enableOcclusionTweaks), "occlusion.MixinWorldRenderer"),

    client_renderdistance_MixinGameSettings(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.raiseMaxRenderDistance), "renderdistance.MixinGameSettings"),
    client_renderdistance_MixinRenderGlobal(Side.CLIENT, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.raiseMaxRenderDistance), "renderdistance.MixinRenderGlobal"),
    common_renderdistance_MixinPlayerManager(Side.COMMON, avoid(TargetedMod.OPTIFINE).and(avoid(TargetedMod.FASTCRAFT)).and(m -> ArchaicConfig.raiseMaxRenderDistance), "renderdistance.MixinPlayerManager"),

    // MOD-FILTERED MIXINS
    common_lighting_fastcraft_MixinChunk(Side.COMMON, require(TargetedMod.FASTCRAFT), "lighting.fastcraft.MixinChunk"),
    common_lighting_fastcraft_MixinChunkProviderServer(Side.COMMON, require(TargetedMod.FASTCRAFT), "lighting.fastcraft.MixinChunkProviderServer"),
    common_lighting_fastcraft_MixinWorld(Side.COMMON, require(TargetedMod.FASTCRAFT), "lighting.fastcraft.MixinWorld"),

    common_mekanism_MixinGenHandler(Side.COMMON, require(TargetedMod.MEKANISM), "mekanism.MixinGenHandler"),

    common_thermal_MixinBlockOre(Side.COMMON, require(TargetedMod.THERMALFOUNDATION), "thermal.MixinBlockOre"),

    common_botania_MixinBlockSpecialFlower(Side.COMMON, require(TargetedMod.BOTANIA), "botania.MixinBlockSpecialFlower"),

    common_extrautils_MixinEventHandlerSiege(Side.COMMON, require(TargetedMod.EXTRAUTILS), "extrautils.MixinEventHandlerSiege"),
    common_extrautils_MixinEventHandlerServer(Side.COMMON, require(TargetedMod.EXTRAUTILS), "extrautils.MixinEventHandlerServer"),
    common_extrautils_MixinItemDivisionSigil(Side.COMMON, require(TargetedMod.EXTRAUTILS), "extrautils.MixinItemDivisionSigil"),

    // The modFilter argument is a predicate, so you can also use the .and(), .or(), and .negate() methods to mix and match multiple predicates.
    ;

    @Getter
    public final Side side;
    @Getter
    public final Predicate<List<ITargetedMod>> filter;
    @Getter
    public final String mixin;
}
