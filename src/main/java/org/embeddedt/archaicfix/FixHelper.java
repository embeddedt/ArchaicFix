package org.embeddedt.archaicfix;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.embeddedt.archaicfix.ducks.IAcceleratedRecipe;
import org.embeddedt.archaicfix.helpers.OreDictIterator;
import org.embeddedt.archaicfix.recipe.IFasterCraftingManager;

import java.util.ArrayList;

public class FixHelper {
    public static ArrayList<IAcceleratedRecipe> recipesHoldingPotentialItems = new ArrayList<>();

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

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        for(IAcceleratedRecipe recipe : recipesHoldingPotentialItems) {
           recipe.invalidatePotentialItems();
        }
        recipesHoldingPotentialItems.clear();
        ((IFasterCraftingManager)CraftingManager.getInstance()).clearRecipeCache();
        OreDictIterator.clearCache(event.Name);
    }
}
