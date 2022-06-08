package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(VillageSiege.class)
public class MixinVillageSiege {
    @Shadow private int field_75536_c;

    @Shadow private Village theVillage;

    @Shadow private World worldObj;

    @Shadow private int field_75532_g, field_75538_h, field_75539_i;

    @Inject(method = "func_75527_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Vec3;createVectorHelper(DDD)Lnet/minecraft/util/Vec3;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void returnSiegeLocation(int startX, int startY, int startZ, CallbackInfoReturnable<Vec3> cir, int l, int vecX, int vecY, int vecZ) {
        cir.setReturnValue(Vec3.createVectorHelper(vecX, vecY, vecZ));
    }

    @Inject(method = "tick", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/village/VillageSiege;field_75535_b:Z"), cancellable = true)
    private void skipForInitialState(CallbackInfo ci) {
        if(this.field_75536_c == -1)
            ci.cancel();
    }

    @Inject(method = "func_75529_b", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/village/VillageSiege;field_75539_i:I", shift = At.Shift.AFTER))
    private void correctlyChooseLocation(CallbackInfoReturnable<Boolean> cir) {
        ChunkCoordinates villageCenterChunk = this.theVillage.getCenter();
        float villageRadius = (float)this.theVillage.getVillageRadius();
        float angle = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
        this.field_75532_g = villageCenterChunk.posX + (int) ((double) (MathHelper.cos(angle) * villageRadius) * 0.9D);
        this.field_75538_h = villageCenterChunk.posY;
        this.field_75539_i = villageCenterChunk.posZ + (int) ((double) (MathHelper.sin(angle) * villageRadius) * 0.9D);
    }
}
