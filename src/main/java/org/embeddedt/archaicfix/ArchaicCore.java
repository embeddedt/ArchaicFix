package org.embeddedt.archaicfix;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.embeddedt.archaicfix.asm.Mixin;
import org.embeddedt.archaicfix.asm.TargetedMod;
import org.embeddedt.archaicfix.asm.transformer.VampirismTransformer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.config.ConfigException;
import org.embeddedt.archaicfix.config.ConfigurationManager;

import java.util.*;

@IFMLLoadingPlugin.Name("ArchaicCore")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ArchaicCore implements IFMLLoadingPlugin, IEarlyMixinLoader {
    static {
        VampirismTransformer.init();
        try {
            ConfigurationManager.registerConfig(ArchaicConfig.class);
        } catch(ConfigException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
            "org.embeddedt.archaicfix.asm.ArchaicTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.archaicfix.early.json";
    }

    public static Set<TargetedMod> coreMods = new HashSet<>();

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        List<String> mixins = new ArrayList<>();
        /* FIXME: handle coremods */
        for(Mixin mixin : Mixin.values()) {
            if(mixin.getPhase() == Mixin.Phase.EARLY && mixin.shouldLoadSide() && mixin.getFilter().test(coreMods)) {
                mixins.add(mixin.getMixin());
            }
        }
        return mixins;
    }
}
