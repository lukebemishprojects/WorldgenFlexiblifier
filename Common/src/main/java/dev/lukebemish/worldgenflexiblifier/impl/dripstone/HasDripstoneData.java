/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.dripstone;

import org.jetbrains.annotations.Nullable;

public interface HasDripstoneData {
    void worldgenflexiblifier$setDripstoneData(DripstoneClusterAlternateData alt);
    @Nullable DripstoneClusterAlternateData worldgenflexiblifier$getDripstoneData();
}
