package org.embeddedt.archaicfix;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraftforge.event.entity.living.LivingEvent;

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
