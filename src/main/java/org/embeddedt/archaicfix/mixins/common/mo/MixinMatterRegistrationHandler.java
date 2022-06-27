package org.embeddedt.archaicfix.mixins.common.mo;

import matteroverdrive.handler.MatterRegistrationHandler;
import matteroverdrive.handler.thread.RegisterItemsFromRecipes;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MatterRegistrationHandler.class)
public abstract class MixinMatterRegistrationHandler {
    @Shadow(remap = false) @Final public String registryPath;

    private Thread theMatterThread = null;

    /**
     * @author embeddedt
     * @reason Use a separate thread with lowered priority to run this task.
     */
    @Overwrite(remap = false)
    public void runCalculationThread() {
        if(theMatterThread != null) {
            ArchaicLogger.LOGGER.warn("Stopping old matter calculation thread");
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
