package org.embeddedt.archaicfix.mixins.pregen.common;

import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pregenerator.impl.processor.generator.ChunkProcessor;

import java.util.TreeSet;

@Mixin(ChunkProcessor.class)
public abstract class MixinChunkProcessor {
    @Shadow(remap = false) public abstract MinecraftServer getServer();

    @Shadow(remap = false) private boolean working;

    @Inject(method = "onServerTickEvent", at = @At(value = "INVOKE", target = "Lpregenerator/impl/misc/DeltaTimer;averageDelta()J", ordinal = 0, remap = false), remap = false, cancellable = true)
    private void checkNumBlockUpdates(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        for(WorldServer world : this.getServer().worldServers) {
            if (world != null) {
                TreeSet<NextTickListEntry> ticks = ReflectionHelper.getPrivateValue(WorldServer.class, world, "field_73065_O", "pendingTickListEntriesTreeSet");
                if(ticks.size() > 5000) {
                    this.working = false;
                    ci.cancel();
                    break;
                }
            }
        }
    }
}
