package dev.lukebemish.worldgenflexiblifier.impl.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;
import java.util.function.Function;

public final class Codecs {
    private Codecs() {}

    public static final Codec<BlockState> BLOCK_STATE =
            Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), BuiltInRegistries.BLOCK.byNameCodec().dispatch("block", BlockState::getBlock, block -> {
                BlockState defaultState = block.defaultBlockState();
                if (defaultState.getValues().isEmpty()) {
                    return Codec.unit(defaultState);
                }
                MapCodec<BlockState> mapCodec = MapCodec.of(Encoder.empty(), Decoder.unit(() -> defaultState));
                for(Property<?> property : defaultState.getProperties()) {
                    mapCodec = addProperty(mapCodec, defaultState, property);
                }
                return mapCodec.codec().optionalFieldOf("properties").xmap(
                        optional -> optional.orElse(defaultState),
                        Optional::of).codec();
            })).xmap(
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

    private static <T extends Comparable<T>> MapCodec<BlockState> addProperty(MapCodec<BlockState> pPropertyCodec, BlockState defaultState, Property<T> property) {
        return Codec.mapPair(pPropertyCodec, property.valueCodec().fieldOf(property.getName())
                .orElseGet(s -> {}, () -> property.value(defaultState)))
                .xmap(
                        pair -> pair.getFirst().setValue(property, pair.getSecond().value()),
                        state -> Pair.of(state, property.value(state)));
    }
}
