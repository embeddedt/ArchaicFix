package org.embeddedt.archaicfix.mixins.client.core;

import cpw.mods.fml.client.SplashProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplashProgress.class)
public interface AccessorSplashProgress {
    @Accessor("barBorderColor")
    static int getBarBorderColor() {
        throw new AssertionError();
    }
    @Accessor("barBackgroundColor")
    static int getBarBackgroundColor() {
        throw new AssertionError();
    }
    @Accessor("fontColor")
    static int getFontColor() {
        throw new AssertionError();
    }
}
