package org.embeddedt.archaicfix.mixins.client.divinerpg;

import net.divinerpg.entities.arcana.projectile.EntitySparkler;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntitySparkler.class)
public abstract class MixinEntitySparkler extends EntityThrowable {
    public MixinEntitySparkler(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityThrowable;onUpdate()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private void onlySpawnParticlesOnClient(CallbackInfo ci) {
       if(!this.worldObj.isRemote)
           ci.cancel();
    }
}
