package org.embeddedt.archaicfix.mixins.common.am2;

import am2.PlayerTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value=PlayerTracker.class, remap=false)
public class MixinPlayerTracker {

    @ModifyConstant(method = "populateAALList", constant = @Constant(stringValue = "http://qorconcept.com/mc/AREW0152.txt"))
    private String disableAAListDownload(String old) {
        return "0.0.0.0";
    }

}
