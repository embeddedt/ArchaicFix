package org.embeddedt.archaicfix.mixins.common.core;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_EarlyXpDrop extends Entity {
    @Shadow public int deathTime;

    @Shadow protected int recentlyHit;

    @Shadow protected abstract boolean isPlayer();

    @Shadow protected abstract boolean func_146066_aG();

    @Shadow protected abstract int getExperiencePoints(EntityPlayer p_70693_1_);

    @Shadow protected EntityPlayer attackingPlayer;

    public MixinEntityLivingBase_EarlyXpDrop(World worldIn) {
        super(worldIn);
    }

    private int arch$droppedXp = 0;

    /**
     * @author embeddedt
     * @reason Drop the XP immediately when the entity first dies, like modern versions.
     */
    @Inject(method = "onDeathUpdate", at = @At("HEAD"))
    private void dropXpImmediately(CallbackInfo ci) {
        if (this.deathTime == 0) {
            if (!this.worldObj.isRemote && (this.recentlyHit > 0 || this.isPlayer()) && this.func_146066_aG() && this.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot"))
            {
                int i = this.getExperiencePoints(this.attackingPlayer);

                arch$droppedXp = i;

                while (i > 0)
                {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            } else {
                arch$droppedXp = 0;
            }
        }
    }

    /**
     * @author embeddedt
     * @reason suppress dropping the XP later
     */
    @ModifyExpressionValue(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getExperiencePoints(Lnet/minecraft/entity/player/EntityPlayer;)I"))
    private int skipXpDrop(int original) {
        return original - arch$droppedXp;
    }
}
