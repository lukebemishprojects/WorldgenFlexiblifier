package dev.lukebemish.worldgenflexiblifier.impl.dripstone;

import org.jetbrains.annotations.Nullable;

public interface HasDripstoneData {
    void worldgenflexiblifier$setAlternativeDripstoneData(DripstoneClusterAlternateData alt);
    @Nullable DripstoneClusterAlternateData worldgenflexiblifier$getDripstoneData();
}