package org.embeddedt.archaicfix;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.embeddedt.archaicfix.helpers.FastutilHelper;

import java.util.Map;

@IFMLLoadingPlugin.Name("ArchaicCore")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class ArchaicCore implements IFMLLoadingPlugin {
    static {
        FastutilHelper.load();
    }
    @Override
    public String[] getASMTransformerClass() {
        return new String[] {
                //"org.embeddedt.archaicfix.asm.ArchaicTransformer"
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
}
