package org.embeddedt.archaicfix.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import org.embeddedt.archaicfix.config.ArchaicConfig;

public class WorldRendererDistanceHelper {
    /**
     * Get the squared distance of this world renderer, adjusted to favor the XZ axes over the Y one.
     * @author embeddedt, makamys
     * @param e render view entity
     * @param instance world renderer
     * @return an adjusted squared distance of this renderer from the entity
     */
    public static double betterDistanceSquared(Entity e, WorldRenderer instance) {
        if(ArchaicConfig.improveRenderSortingOrder) {
            double xDiff = e.posX - instance.posXPlus;
            double yDiff = e.posY - instance.posYPlus;
            double zDiff = e.posZ - instance.posZPlus;
            return (Math.pow(xDiff, 2) + Math.pow(yDiff * Minecraft.getMinecraft().gameSettings.renderDistanceChunks / 2, 2) + Math.pow(zDiff, 2));
        } else
            return instance.distanceToEntitySquared(e);
    }
}
