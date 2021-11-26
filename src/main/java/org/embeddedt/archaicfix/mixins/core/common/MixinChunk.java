package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Chunk.class)
public class MixinChunk {
    @Shadow @Final private List<Entity>[] entityLists;
    @Shadow @Final private World worldObj;

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

}