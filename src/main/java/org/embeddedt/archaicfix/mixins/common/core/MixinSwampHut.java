package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.world.gen.structure.ComponentScatteredFeaturePieces;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentScatteredFeaturePieces.SwampHut.class)
public class MixinSwampHut {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/EntityWitch;onSpawnWithEgg(Lnet/minecraft/entity/IEntityLivingData;)Lnet/minecraft/entity/IEntityLivingData;"))
    private IEntityLivingData summonWitchWithPersistence(EntityWitch instance, IEntityLivingData iEntityLivingData) {
        instance.func_110163_bv();
        return instance.onSpawnWithEgg(iEntityLivingData);
    }
}
