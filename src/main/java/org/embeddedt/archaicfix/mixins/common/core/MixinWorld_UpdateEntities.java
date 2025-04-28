package org.embeddedt.archaicfix.mixins.common.core;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(World.class)
public abstract class MixinWorld_UpdateEntities {
    @Redirect(method = "updateEntities", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/tileentity/TileEntity;onChunkUnload()V", remap = false)), at = @At(value = "INVOKE", target = "Ljava/util/List;removeAll(Ljava/util/Collection;)Z", ordinal = 0))
    private boolean removeInUnloaded(List<TileEntity> instance, Collection<TileEntity> objects) {
        if (ArchaicConfig.fixTEUnloadLag) {
            // Arbitrary number chosen because contains() will be fast enough on a tiny list
            if(objects.size() > 3) {
                Set<TileEntity> toRemove = Collections.newSetFromMap(new IdentityHashMap<>(objects.size()));
                toRemove.addAll(objects);
                objects = toRemove;
            }
        }
        return instance.removeAll(objects);
    }
}
