package org.embeddedt.archaicfix.mixins.common.core;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.ObjectIntIdentityMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.List;

@SuppressWarnings("rawtypes")
@Mixin(ObjectIntIdentityMap.class)
public class MixinObjectIntIdentityMap {

    @Shadow protected List field_148748_b;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initIdArray(CallbackInfo ci) {
        this.field_148748_b = new ObjectArrayList();
    }

    /**
     * @author embeddedt
     * @reason Avoid unnecessary range checks
     * @param id ID
     * @return object if ID is valid, else null
     */
    @Overwrite
    public Object func_148745_a(int id) {
        Object[] backingArray = ((ObjectArrayList)this.field_148748_b).elements();
        if(id >= 0 && id < backingArray.length)
            return backingArray[id];
        else
            return null;
    }
}
