package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry {
    @Shadow public int lastScaledXPosition, lastScaledYPosition, lastScaledZPosition;

    @Shadow public int lastYaw;

    @Shadow public int lastPitch;

    @Shadow public Entity myEntity;

    @Inject(method = "sendLocationToAllClients", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityTrackerEntry;sendMetadataToAllAssociatedPlayers()V", ordinal = 1, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void saveIfTeleported(List clientList, CallbackInfo ci, int i, int j, int k, int l, int i1, int j1, int k1, int l1, Object object) {
        if(object instanceof S18PacketEntityTeleport) {
            this.lastScaledXPosition = i;
            this.lastScaledYPosition = j;
            this.lastScaledZPosition = k;
            this.lastYaw = l;
            this.lastPitch = i1;
        }
    }

    @Inject(method = "func_151260_c", at = @At("RETURN"), cancellable = true)
    private void useCorrectSpawnPosition(CallbackInfoReturnable<Packet> cir) {
        if (!(this.myEntity instanceof EntityItemFrame) && !(this.myEntity instanceof EntityLeashKnot)) {
            Packet packet = cir.getReturnValue();
            if(packet instanceof S0EPacketSpawnObject) {
                S0EPacketSpawnObject spawnObject = (S0EPacketSpawnObject)packet;
                spawnObject.func_148996_a(this.lastScaledXPosition);
                spawnObject.func_148995_b(this.lastScaledYPosition);
                spawnObject.func_149005_c(this.lastScaledZPosition);
            } else if(packet instanceof S0CPacketSpawnPlayer) {
                S0CPacketSpawnPlayer spawnPlayer = (S0CPacketSpawnPlayer)packet;
                spawnPlayer.field_148956_c = this.lastScaledXPosition;
                spawnPlayer.field_148953_d = this.lastScaledYPosition;
                spawnPlayer.field_148954_e = this.lastScaledZPosition;
            } else if(packet instanceof S11PacketSpawnExperienceOrb) {
                S11PacketSpawnExperienceOrb spawnXpOrb = (S11PacketSpawnExperienceOrb)packet;
                spawnXpOrb.field_148990_b = this.lastScaledXPosition;
                spawnXpOrb.field_148991_c = this.lastScaledYPosition;
                spawnXpOrb.field_148988_d = this.lastScaledZPosition;
            }
        }
    }
}
