package org.embeddedt.archaicfix;

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;

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
    }

}