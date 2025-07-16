package org.embeddedt.archaicfix;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;
import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.embeddedt.archaicfix.asm.Mixin;
import org.embeddedt.archaicfix.asm.transformer.VampirismTransformer;
import org.embeddedt.archaicfix.config.ArchaicConfig;
import org.embeddedt.archaicfix.config.ConfigException;
import org.embeddedt.archaicfix.config.ConfigurationManager;
import org.embeddedt.archaicfix.helpers.LetsEncryptHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@IFMLLoadingPlugin.Name("ArchaicCore")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ArchaicCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    public static final Logger LOGGER = LogManager.getLogger("ArchaicCore");

    static {
        VampirismTransformer.init();
        try {
            ConfigurationManager.registerConfig(ArchaicConfig.class);
        } catch (ConfigException e) {
            throw new RuntimeException(e);
        }
        LetsEncryptHelper.replaceSSLContext();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
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

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return IMixins.getEarlyMixins(Mixin.class, loadedCoreMods);
    }
}
