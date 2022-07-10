package org.embeddedt.archaicfix.mixins.common.core;

import net.minecraft.world.WorldServer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.helpers.WorldServerHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashSet;
import java.util.Set;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @Shadow private Set pendingTickListEntriesHashSet;

    @ModifyConstant(method = "tickUpdates", constant = @Constant(intValue = 1000), expect = 2, require = 0)
    private int increaseUpdateLimit(int old) {
        return ArchaicConfig.increaseBlockUpdateLimit ? 65000 : old;
    }

    @Redirect(method = { "<init>", "initialize" }, at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/world/WorldServer;pendingTickListEntriesHashSet:Ljava/util/Set;"))
    private void useFixedTickHashSet(WorldServer instance, Set value) {
        this.pendingTickListEntriesHashSet = WorldServerHelper.NextTickListEntryHashSet.newHashSet();
    }
}
