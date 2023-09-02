package dev.lukebemish.worldgenflexiblifier.impl.oreveins;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface HasOreVeins {
    void worldgenflexiblifier$setOreVeins(List<OreVein> oreVeins);

    @Nullable List<OreVein> worldgenflexiblifier$getOreVeins();
}
