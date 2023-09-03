/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl;

import net.minecraft.resources.ResourceLocation;

public class Constants {
    public static final String MOD_ID = "worldgenflexiblifier";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
