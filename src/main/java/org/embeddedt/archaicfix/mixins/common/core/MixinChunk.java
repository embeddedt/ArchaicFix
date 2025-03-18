package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.embeddedt.archaicfix.ducks.ICascadeDetectionChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = Chunk.class, priority = 1100)
public abstract class MixinChunk implements ICascadeDetectionChunk {
    @Shadow @Final private List<Entity>[] entityLists;
    @Shadow @Final private World worldObj;

    @Shadow
    protected abstract void populateChunk(IChunkProvider provider1, IChunkProvider provider2, int x, int z);

    @Inject(method = "onChunkUnload", at = @At("HEAD"))
    public void handlePlayerChunkUnload(CallbackInfo ci) {
        final List<EntityPlayer> players = new ArrayList<>();
        for (final List<Entity> list : entityLists) {
            for(final Entity entity : list) {
                if(entity instanceof EntityPlayer)
                    players.add((EntityPlayer)entity);
            }
        }
        for (final EntityPlayer player : players) {
            worldObj.updateEntityWithOptionalForce(player, false);
        }
    }

    @Inject(method = "getBiomeGenForWorldCoords", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/WorldChunkManager;getBiomeGenAt(II)Lnet/minecraft/world/biome/BiomeGenBase;"), cancellable = true)
    private void avoidBiomeGenOnClient(int p_76591_1_, int p_76591_2_, WorldChunkManager p_76591_3_, CallbackInfoReturnable<BiomeGenBase> cir) {
        if (this.worldObj.isRemote) {
            cir.setReturnValue(BiomeGenBase.ocean);
        }
    }
}