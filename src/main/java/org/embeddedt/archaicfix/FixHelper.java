package org.embeddedt.archaicfix;

import com.google.common.collect.BiMap;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FixHelper {
    @SubscribeEvent
    public void onSizeUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;
        if(entity.worldObj.isRemote && entity instanceof EntitySlime && entity.getAge() <= 1) {
            EntitySlime slime = (EntitySlime)entity;
            float newSize = 0.6F * (float)slime.getSlimeSize();
            slime.width = newSize;
            slime.height = newSize;
            slime.setPosition(slime.posX, slime.posY, slime.posZ);
        }
    }
}
