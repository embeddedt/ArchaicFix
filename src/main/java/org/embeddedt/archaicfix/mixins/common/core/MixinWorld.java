package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.storage.WorldInfo;
import org.embeddedt.archaicfix.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        float factor = (float)this.worldInfo.getWorldTotalTime() / 7200000.0F;
        factor += this.getCurrentMoonPhaseFactor() * 0.25F;

        if (this.difficultySetting == EnumDifficulty.EASY || this.difficultySetting == EnumDifficulty.PEACEFUL)
        {
            factor *= 0.5F;
        }
        else if (this.difficultySetting == EnumDifficulty.HARD)
        {
            factor *= 2.0F;
        }

        cir.setReturnValue(MathHelper.clamp_float(factor, 0.0F, (float)this.difficultySetting.getDifficultyId() * 0.5F));
    }
}
