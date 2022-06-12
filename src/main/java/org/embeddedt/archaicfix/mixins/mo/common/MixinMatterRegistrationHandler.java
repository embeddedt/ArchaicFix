package org.embeddedt.archaicfix.mixins.mo.common;

import matteroverdrive.handler.MatterRegistrationHandler;
import matteroverdrive.handler.thread.RegisterItemsFromRecipes;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.ArchaicFix;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.File;

@Mixin(MatterRegistrationHandler.class)
public abstract class MixinMatterRegistrationHandler {
    @Shadow @Final public String registryPath;

    private Thread theMatterThread = null;

    /**
     * @author embeddedt
     * @reason Use a separate thread with lowered priority to run this task.
     */
    @Overwrite(remap = false)
    public void runCalculationThread() {
        if(theMatterThread != null) {
            ArchaicFix.LOGGER.warn("Stopping old matter calculation thread");
            theMatterThread.interrupt();
            theMatterThread = null;
        }
        RegisterItemsFromRecipes task = new RegisterItemsFromRecipes(this.registryPath);
        theMatterThread = new Thread(task);
        theMatterThread.setPriority(Thread.MIN_PRIORITY);
        theMatterThread.setName("MO Recipe Calculation");
        theMatterThread.start();
    }
}
