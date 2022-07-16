package org.embeddedt.archaicfix.mixins.common.core;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.ForgeChunkManager;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow public boolean isRemote;

    @Shadow public EnumDifficulty difficultySetting;

    @Shadow public abstract float getCurrentMoonPhaseFactor();

    @Shadow protected WorldInfo worldInfo;

    @Redirect(method = "getBiomeGenForCoordsBody", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/WorldChunkManager;getBiomeGenAt(II)Lnet/minecraft/world/biome/BiomeGenBase;"))
    private BiomeGenBase skipBiomeGenOnClient(WorldChunkManager manager, int x, int z) {
        if(this.isRemote)
            return BiomeGenBase.ocean;
        else
            return manager.getBiomeGenAt(x, z);
    }

    /**
     * @reason Remove regional difficulty and make it based on overall world time instead. (From TMCW)
     * @author embeddedt
     */
    @Inject(method = "func_147473_B", at = @At("HEAD"), cancellable = true)
    public void func_147473_B(int p_147473_1_, int p_147473_2_, int p_147473_3_, CallbackInfoReturnable<Float> cir) {
        if(!ArchaicConfig.betterRegionalDifficulty)
            return;
        float factor = this.worldInfo != null ? ((float)this.worldInfo.getWorldTotalTime() / 7200000.0F) : 0;
        factor += this.getCurrentMoonPhaseFactor() * 0.25F;

        EnumDifficulty difficulty = this.difficultySetting;
        if(difficulty == null)
            difficulty = EnumDifficulty.NORMAL;

        if (difficulty == EnumDifficulty.EASY || difficulty == EnumDifficulty.PEACEFUL)
        {
            factor *= 0.5F;
        }
        else if (difficulty == EnumDifficulty.HARD)
        {
            factor *= 2.0F;
        }

        cir.setReturnValue(MathHelper.clamp_float(factor, 0.0F, (float)difficulty.getDifficultyId() * 0.5F));
    }

    private Set<String> entityOptimizationIgnoreSet = null;

    @Inject(method = "updateEntityWithOptionalForce", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;lastTickPosX:D", ordinal = 0), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void skipUpdateIfOptimizing(Entity entity, boolean force, CallbackInfo ci, int chunkX, int chunkZ, boolean isInForcedChunk) {
        if(!ArchaicConfig.optimizeEntityTicking)
            return;
        if(entityOptimizationIgnoreSet == null)
            entityOptimizationIgnoreSet = ImmutableSet.copyOf(ArchaicConfig.optimizeEntityTickingIgnoreList);
        if(entityOptimizationIgnoreSet.contains(EntityList.getEntityString(entity)))
            return;
        if (isInForcedChunk || !(entity instanceof EntityLivingBase) || entity instanceof EntityPlayer) {
            return;
        }
        double finalDist = Double.MAX_VALUE;
        for(EntityPlayer player : (List<EntityPlayer>)entity.worldObj.playerEntities) {
            finalDist = Math.min(finalDist, player.getDistanceSq(entity.posX, entity.posY, entity.posZ));
            if(finalDist <= ArchaicConfig.optimizeEntityTickingDistance)
                break;
        }
        if(((EntityLivingBase)entity).deathTime <= 0 && finalDist > ArchaicConfig.optimizeEntityTickingDistance) {
            ci.cancel();
        }
    }
}
