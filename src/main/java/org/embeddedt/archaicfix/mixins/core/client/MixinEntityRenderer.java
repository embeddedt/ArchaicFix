package org.embeddedt.archaicfix.mixins.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRenderer.class, priority = 1500)
public abstract class MixinEntityRenderer {
    @Shadow private Minecraft mc;

    @Shadow private float torchFlickerX;

    @Shadow private float bossColorModifier;

    @Shadow private float bossColorModifierPrev;

    @Shadow protected abstract float getNightVisionBrightness(EntityPlayer p_82830_1_, float p_82830_2_);

    @Shadow @Final private int[] lightmapColors;

    @Shadow @Final private DynamicTexture lightmapTexture;

    @Shadow private boolean lightmapUpdateNeeded;

    /** @reason Makes the third-person view camera pass through non-solid blocks (fixes https://bugs.mojang.com/browse/MC-30845) */
    @Redirect(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"))
    private MovingObjectPosition rayTraceBlocks(WorldClient world, Vec3 from, Vec3 to) {
        return world.func_147447_a(from, to, false, true, true);
    }

    @Overwrite
    private void updateLightmap(float par1)
    {
        WorldClient var2 = this.mc.theWorld;

        if (var2 != null)
        {
            for (int var3 = 0; var3 < 256; ++var3)
            {
                float var4 = var2.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float var5 = var2.provider.lightBrightnessTable[var3 / 16] * var4;
                float var6 = var2.provider.lightBrightnessTable[var3 % 16] * (this.torchFlickerX * 0.1F + 1.5F);

                if (var2.lastLightningBolt > 0)
                {
                    var5 = var2.provider.lightBrightnessTable[var3 / 16];
                }

                float var7 = var5 * (var2.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float var8 = var5 * (var2.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float var11 = var6 * ((var6 * 0.6F + 0.4F) * 0.6F + 0.4F);
                float var12 = var6 * (var6 * var6 * 0.6F + 0.4F);
                float var13 = var7 + var6;
                float var14 = var8 + var11;
                float var15 = var5 + var12;
                var13 = var13 * 0.96F + 0.03F;
                var14 = var14 * 0.96F + 0.03F;
                var15 = var15 * 0.96F + 0.03F;
                float var16;

                if (this.bossColorModifier > 0.0F)
                {
                    var16 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * par1;
                    var13 = var13 * (1.0F - var16) + var13 * 0.7F * var16;
                    var14 = var14 * (1.0F - var16) + var14 * 0.6F * var16;
                    var15 = var15 * (1.0F - var16) + var15 * 0.6F * var16;
                }

                if (var2.provider.dimensionId == 1)
                {
                    var13 = 0.22F + var6 * 0.75F;
                    var14 = 0.28F + var11 * 0.75F;
                    var15 = 0.25F + var12 * 0.75F;
                }

                float var17;

                if (this.mc.thePlayer.isPotionActive(Potion.nightVision))
                {
                    var16 = this.getNightVisionBrightness(this.mc.thePlayer, par1);
                    var17 = 1.0F / var13;

                    if (var17 > 1.0F / var14)
                    {
                        var17 = 1.0F / var14;
                    }

                    if (var17 > 1.0F / var15)
                    {
                        var17 = 1.0F / var15;
                    }

                    var13 = var13 * (1.0F - var16) + var13 * var17 * var16;
                    var14 = var14 * (1.0F - var16) + var14 * var17 * var16;
                    var15 = var15 * (1.0F - var16) + var15 * var17 * var16;
                }

                if (var13 > 1.0F)
                {
                    var13 = 1.0F;
                }

                if (var14 > 1.0F)
                {
                    var14 = 1.0F;
                }

                if (var15 > 1.0F)
                {
                    var15 = 1.0F;
                }

                var16 = this.mc.gameSettings.gammaSetting;
                var17 = 1.0F - var13;
                float var18 = 1.0F - var14;
                float var19 = 1.0F - var15;
                var17 = 1.0F - var17 * var17 * var17 * var17;
                var18 = 1.0F - var18 * var18 * var18 * var18;
                var19 = 1.0F - var19 * var19 * var19 * var19;
                var13 = var13 * (1.0F - var16) + var17 * var16;
                var14 = var14 * (1.0F - var16) + var18 * var16;
                var15 = var15 * (1.0F - var16) + var19 * var16;
                var13 = var13 * 0.96F + 0.03F;
                var14 = var14 * 0.96F + 0.03F;
                var15 = var15 * 0.96F + 0.03F;

                if (var13 > 1.0F)
                {
                    var13 = 1.0F;
                }

                if (var14 > 1.0F)
                {
                    var14 = 1.0F;
                }

                if (var15 > 1.0F)
                {
                    var15 = 1.0F;
                }

                if (var13 < 0.0F)
                {
                    var13 = 0.0F;
                }

                if (var14 < 0.0F)
                {
                    var14 = 0.0F;
                }

                if (var15 < 0.0F)
                {
                    var15 = 0.0F;
                }

                short var20 = 255;
                int var21 = (int)(var13 * 255.0F);
                int var22 = (int)(var14 * 255.0F);
                int var23 = (int)(var15 * 255.0F);
                this.lightmapColors[var3] = var20 << 24 | var21 << 16 | var22 << 8 | var23;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
        }
    }
}