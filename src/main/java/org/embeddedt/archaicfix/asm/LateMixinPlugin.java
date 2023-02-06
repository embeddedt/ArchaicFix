package org.embeddedt.archaicfix.asm;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import org.embeddedt.archaicfix.ArchaicCore;

import java.util.*;

@LateMixin
public class LateMixinPlugin implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.archaicfix.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        Set<TargetedMod> validMods = new HashSet<>(ArchaicCore.coreMods);
        HashMap<String, TargetedMod> modById = new HashMap<>();
        for(TargetedMod t : TargetedMod.values()) {
            if(t.getModId() != null)
                modById.put(t.getModId(), t);
        }
        for(String modId : loadedMods) {
            TargetedMod t = modById.get(modId);
            if(t != null)
                validMods.add(t);
        }
        for(Mixin mixin : Mixin.values()) {
            if(mixin.getPhase() == Mixin.Phase.LATE && mixin.getFilter().test(validMods))
                mixins.add(mixin.getMixin());
        }
        return mixins;
    }
}
