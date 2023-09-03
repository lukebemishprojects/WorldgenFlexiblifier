/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.oreveins;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OreVeinFeature extends Feature<OreVeinConfiguration> {
    public OreVeinFeature(Codec<OreVeinConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OreVeinConfiguration> context) {
        RandomSource randomSource = context.random();
        var positionalFactory = randomSource.forkPositional();
        DensityFunction.Visitor visitor = new DensityFunction.Visitor() {
            @Override
            public @NotNull DensityFunction apply(@NotNull DensityFunction densityFunction) {
                return densityFunction;
            }

            @Override
            public DensityFunction.@NotNull NoiseHolder visitNoise(DensityFunction.NoiseHolder noiseHolder) {
                @Nullable ResourceKey<NormalNoise.NoiseParameters> key = noiseHolder.noiseData().unwrapKey().orElseThrow();
                NormalNoise noise = context.level().getLevel().getChunkSource().randomState().getOrCreateNoise(key);
                return new DensityFunction.NoiseHolder(noiseHolder.noiseData(), noise);
            }
        };

        var config = context.config().mapAll(visitor);
        var chunk = context.level().getChunk(context.origin());
        var noiseContext = new MutableNoiseContext();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = chunk.getPos().getMinBlockX(); x <= chunk.getPos().getMaxBlockX(); x++) {
            pos.setX(x);
            noiseContext.x = x;
            for (int z = chunk.getPos().getMinBlockZ(); z <= chunk.getPos().getMaxBlockZ(); z++) {
                pos.setZ(z);
                noiseContext.z = z;
                for (int y = config.placement().maxY(); y >= config.placement().minY(); y--) {
                    pos.setY(y);
                    if (chunk.getBlockState(pos).is(config.replace())) {
                        noiseContext.y = y;
                        BlockState state = config.create(positionalFactory, pos, noiseContext);
                        if (state != null) {
                            chunk.setBlockState(pos, state, false);
                        }
                    }
                }
            }
        }
        return true;
    }

    private static class MutableNoiseContext implements DensityFunction.FunctionContext {
        private int x;
        private int y;
        private int z;

        @Override
        public int blockX() {
            return x;
        }

        @Override
        public int blockY() {
            return y;
        }

        @Override
        public int blockZ() {
            return z;
        }
    }
}
