package org.embeddedt.archaicfix.mixins.common.core;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.WorldInfo;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.lighting.world.lighting.LightingEngineHelpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(World.class)
public abstract class MixinWorld {
    @Shadow public boolean isRemote;

    @Shadow public EnumDifficulty difficultySetting;

    @Shadow public abstract float getCurrentMoonPhaseFactor();

    @Shadow protected WorldInfo worldInfo;

    @Shadow public List playerEntities;

    @Shadow public abstract Chunk getChunkFromChunkCoords(int p_72964_1_, int p_72964_2_);

    @Shadow protected IChunkProvider chunkProvider;

    //Initially populate it with ridiculous values since if it was populated with 0 or -1 by default it might produce a false negative, and return null in the cache.
    private final long[] chunkPositionsCache = new long[]{Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, Long.MAX_VALUE};

    private final Chunk[] chunkCache = new Chunk[4];

    @WrapOperation(method = "getChunkFromChunkCoords", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/IChunkProvider;provideChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk cacheChunkFetch(IChunkProvider instance, int x, int z, Operation<Chunk> original) {
        if(!ArchaicConfig.chunkFetchCache || isRemote) { //Don't run this if the config is off or if this is a client world
            return original.call(instance, x, z);
        }

		for (int i = 0; i < chunkPositionsCache.length; i++) {
			long packedChunkCoords = chunkPositionsCache[i];
			int checkX = (int) packedChunkCoords;
			int checkZ = (int) (packedChunkCoords >> 32);
			if (checkX == x && checkZ == z && chunkCache[i] != null) {
                return chunkCache[i]; //Found chunk in cache! Don't run the provider again!!!
			}
		}
        //Chunk not found in cache, add it to the cache and return it.
        Chunk chunk = original.call(instance, x, z);
        updateCaches(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
        return chunk;
    }

    /// Push the previous highest value out of the caches and add the new ones at the top.
    private void updateCaches(long coords, Chunk chunk) {
        chunkCache[3] = chunkCache[2];
        chunkCache[2] = chunkCache[1];
        chunkCache[1] = chunkCache[0];
        chunkCache[0] = chunk;

        chunkPositionsCache[3] = chunkPositionsCache[2];
        chunkPositionsCache[2] = chunkPositionsCache[1];
        chunkPositionsCache[1] = chunkPositionsCache[0];
        chunkPositionsCache[0] = coords;
    }

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
        if (isRemote || isInForcedChunk || !(entity instanceof EntityLivingBase) || entity instanceof EntityPlayer || !entity.addedToChunk) {
            return;
        }
        if(entityOptimizationIgnoreSet == null)
            entityOptimizationIgnoreSet = ImmutableSet.copyOf(ArchaicConfig.optimizeEntityTickingIgnoreList);
        if(entityOptimizationIgnoreSet.contains(EntityList.getEntityString(entity)))
            return;
        double finalDist = Double.MAX_VALUE;
        for(EntityPlayer player : (List<EntityPlayer>)entity.worldObj.playerEntities) {
            finalDist = Math.min(finalDist, player.getDistanceSq(entity.posX, entity.posY, entity.posZ));
            if(finalDist <= ArchaicConfig.optimizeEntityTickingDistance)
                break;
        }
        if(((EntityLivingBase)entity).deathTime <= 0 && finalDist > ArchaicConfig.optimizeEntityTickingDistance) {
            if(entity instanceof EntityLiving && ((AccessorEntityLiving)entity).invokeCanDespawn()) {
                /* Despawn even if outside the ticking range */
                ((AccessorEntityLiving)entity).invokeDespawnEntity();
            }
            ci.cancel();
        }
    }

    @Inject(method = "setActivePlayerChunksAndCheckLight", at = @At("TAIL"))
    private void saveInactiveChunks(CallbackInfo ci) {
        if(this.isRemote || ArchaicConfig.optimizeBlockTickingDistance <= 0)
            return;
        int renderDistance = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getViewDistance();
        if(renderDistance <= ArchaicConfig.optimizeBlockTickingDistance)
            return;
        EntityPlayer entityplayer;
        int j;
        int k;

        int activeDistance = ArchaicConfig.optimizeBlockTickingDistance;

        for (int i = 0; i < this.playerEntities.size(); ++i)
        {
            entityplayer = (EntityPlayer)this.playerEntities.get(i);
            j = MathHelper.floor_double(entityplayer.posX / 16.0D);
            k = MathHelper.floor_double(entityplayer.posZ / 16.0D);

            for (int offX = -renderDistance; offX <= renderDistance; ++offX)
            {
                for (int offZ = -renderDistance; offZ <= renderDistance; ++offZ)
                {
                    if(Math.abs(offX) <= activeDistance && Math.abs(offZ) <= activeDistance)
                        continue;
                    Chunk chunk = LightingEngineHelpers.getLoadedChunk(this.chunkProvider, offX + j, offZ + k);
                    if(chunk != null && !chunk.func_150802_k()) {
                        chunk.func_150804_b(false);
                    }
                }
            }
        }
    }

    @Redirect(method = "updateEntities", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;onChunkUnload()V")), at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z", ordinal = 0))
    private boolean removeInUnloaded(List<?> instance, Collection<?> objects) {
        if (ArchaicConfig.fixTEUnloadLag) {
            // Arbitrary number chosen because contains() will be fast enough on a tiny list
            if(objects.size() > 3) {
                Set toRemove = Collections.newSetFromMap(new IdentityHashMap<>(objects.size()));
                toRemove.addAll(objects);
                objects = toRemove;
            }
        }
        return instance.removeAll(objects);
    }
}
