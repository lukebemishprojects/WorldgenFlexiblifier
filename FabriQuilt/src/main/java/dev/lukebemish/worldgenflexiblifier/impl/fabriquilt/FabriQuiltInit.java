/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.fabriquilt;

import dev.lukebemish.worldgenflexiblifier.impl.Constants;
import dev.lukebemish.worldgenflexiblifier.impl.WorldgenFlexiblifier;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("deprecation")
public class FabriQuiltInit implements ModInitializer {
    @Override
    public void onInitialize() {
        WorldgenFlexiblifier.registerFeatures((string, feature) ->
            Registry.register(BuiltInRegistries.FEATURE, new ResourceLocation(Constants.MOD_ID, string), feature.get()));
    }
}
