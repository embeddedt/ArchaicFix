package org.embeddedt.archaicfix.mixins.client.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderSorter;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderSorter.class)
public class MixinRenderSorter {
    @Redirect(method = "compare(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/client/renderer/WorldRenderer;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;distanceToEntitySquared(Lnet/minecraft/entity/Entity;)F"))
    public float compare(WorldRenderer instance, Entity e) {
        double xDiff = e.posX - instance.posXPlus;
        double yDiff = e.posY - instance.posYPlus;
        double zDiff = e.posZ - instance.posZPlus;
        return (float)(xDiff * xDiff + yDiff * yDiff * Minecraft.getMinecraft().gameSettings.renderDistanceChunks / 2 + zDiff * zDiff);
    }
}
