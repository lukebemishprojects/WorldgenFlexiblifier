/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.HasDripstoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.PointedDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(PointedDripstoneFeature.class)
public class PointedDripstoneFeatureMixin {
    @WrapOperation(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/PointedDripstoneFeature;getTipDirection(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)Ljava/util/Optional;"
        )
    )
    private Optional<Direction> worldgenflexiblifier$wrapTipDirection(LevelAccessor level, BlockPos pos, RandomSource random, Operation<Optional<Direction>> operation, FeaturePlaceContext<PointedDripstoneConfiguration> context) {
        var config = context.config();
        var data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data != null && !data.isDefault()) {
            return data.getTipDirection(level, pos, random);
        }
        return operation.call(level, pos, random);
    }

    @WrapOperation(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/DripstoneUtils;growPointedDripstone(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;IZ)V"
        )
    )
    private void worldgenflexiblifier$wrapGrowPointedDripstone(LevelAccessor level, BlockPos pos, Direction direction, int height, boolean mergeTip, Operation<Void> operation, FeaturePlaceContext<PointedDripstoneConfiguration> context) {
        var config = context.config();
        var data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data != null && !data.isDefault()) {
            data.growPointedDripstone(level, pos, direction, height, mergeTip);
            return;
        }
        operation.call(level, pos, direction, height, mergeTip);
    }

    @WrapOperation(
        method = "createPatchOfDripstoneBlocks",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/DripstoneUtils;placeDripstoneBlockIfPossible(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Z"
        ),
        expect = 4
    )
    private static boolean worldgenflexiblifier$wrapCreateBlocks(LevelAccessor level, BlockPos pos, Operation<Boolean> operation, LevelAccessor level2, RandomSource random, BlockPos pos2, PointedDripstoneConfiguration config) {
        var data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data != null && !data.isDefault()) {
            return data.placeDripstoneBlockIfPossible(level, pos);
        }
        return operation.call(level, pos);
    }
}
