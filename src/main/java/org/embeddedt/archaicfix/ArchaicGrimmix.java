package org.embeddedt.archaicfix;

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.MixinEnvironment;

@Grimmix(id = "archaicgrimmix", name = "Grimmix for ArchaicFix")
public class ArchaicGrimmix extends GrimmixController {
    @Override
    public void buildMixinConfigs(IConfigBuildingEvent event) {
        event.createBuilder("archaicfix/mixins.archaicfix.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.core")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .verbose(true)
                .required(true)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.lighting.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.lighting")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .verbose(true)
                .required(true)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.pregen.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.pregen")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.gt6.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.gt6")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.projecte.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.projecte")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.mrtjpcore.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.mrtjp")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.matteroverdrive.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.mo")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.tc4tweaks.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.tc4tweaks")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.thermal.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.thermal")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        event.createBuilder("archaicfix/mixins.archaicfix.chickenchunks.json")
                .mixinPackage("org.embeddedt.archaicfix.mixins.chickenchunks")
                .commonMixins("common.*")
                .clientMixins("client.*")
                .refmap("@MIXIN_REFMAP@")
                .configurationType(ConfigurationType.MOD)
                .verbose(true)
                .required(false)
                .build();
        if((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))
            MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT).setOption(MixinEnvironment.Option.DEBUG_INJECTORS, false);
    }

}