package org.embeddedt.archaicfix.mixins.common.pregen;

import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import org.embeddedt.archaicfix.ArchaicLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pregenerator.impl.processor.generator.ChunkProcessor;

import java.util.concurrent.TimeUnit;

@Mixin(ChunkProcessor.class)
public abstract class MixinChunkProcessor {
    @Shadow(remap = false) public abstract MinecraftServer getServer();

    @Shadow(remap = false) private boolean working;

    private long lastTimeMessaged = System.nanoTime();

    @Inject(method = "onServerTickEvent", at = @At(value = "INVOKE", target = "Lpregenerator/impl/misc/DeltaTimer;averageDelta()J", ordinal = 0, remap = false), remap = false, cancellable = true)
    private void checkNumBlockUpdates(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        for(WorldServer world : this.getServer().worldServers) {
            if (world != null) {
                if(world.pendingTickListEntriesTreeSet.size() > 500000) {
                    this.working = false;
                    long elapsed = TimeUnit.SECONDS.convert(System.nanoTime() - lastTimeMessaged, TimeUnit.NANOSECONDS);
                    if(elapsed >= 5) {
                        lastTimeMessaged = System.nanoTime();
                        ArchaicLogger.LOGGER.warn("Preventing more pregeneration till the update queue settles in dimension " + world.provider.dimensionId);
                    }
                    ci.cancel();
                    break;
                }
            }
        }
    }
}
