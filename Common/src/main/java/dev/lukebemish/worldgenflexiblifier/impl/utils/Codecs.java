/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.function.Function;

public final class Codecs {
    private Codecs() {}

    public static final Codec<BlockState> BLOCK_STATE =
            Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), BlockState.CODEC).xmap(
                either -> either.map(Block::defaultBlockState, Function.identity()),
                state -> {
                    BlockState defaultState = state.getBlock().defaultBlockState();
                    for (Property<?> property : defaultState.getProperties()) {
                        if (!state.getValue(property).equals(defaultState.getValue(property))) {
                            return Either.right(state);
                        }
                    }
                    return Either.left(state.getBlock());
                });
}
