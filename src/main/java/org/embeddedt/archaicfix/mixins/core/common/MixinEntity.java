package org.embeddedt.archaicfix.mixins.core.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow public float fallDistance;

    @Shadow @Final public AxisAlignedBB boundingBox;

    /**
     * @reason Fixes a vanilla bug where the entity's fall distance is not updated before invoking the
     * block's onFallenUpon when it falls on the ground, meaning that the last fall state update won't
     * be included in the fall distance.
     */
    @Inject(method = "updateFallState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;fall(F)V"))
    private void beforeOnFallenUpon(double distanceFallenThisTick, boolean isOnGround, CallbackInfo ci) {
        if (distanceFallenThisTick < 0) fallDistance -= distanceFallenThisTick;
    }

    private AxisAlignedBB arch$savedBB;
    @Inject(method = "moveEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getCollidingBoundingBoxes(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;", ordinal = 0, shift = At.Shift.AFTER),
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;stepHeight:F", ordinal = 1))
    )
    private void saveOldBoundingBox(double x, double y, double z, CallbackInfo ci) {
        arch$savedBB = this.boundingBox.copy();
        this.boundingBox.setBB(this.boundingBox.expand(x, 0, z));
    }

    @Inject(method = "moveEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/AxisAlignedBB;offset(DDD)Lnet/minecraft/util/AxisAlignedBB;", ordinal = 0, shift = At.Shift.BEFORE),
            slice = @Slice(from = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/Entity;stepHeight:F", ordinal = 1))
    )
    private void restoreOldBoundingBox(double x, double y, double z, CallbackInfo ci) {
        this.boundingBox.setBB(arch$savedBB);
        arch$savedBB = null;
    }
}