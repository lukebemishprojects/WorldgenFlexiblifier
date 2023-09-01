package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.DripstoneClusterAlternateData;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.HasDripstoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DripstoneClusterFeature.class)
public abstract class DripstoneClusterFeatureMixin extends Feature<DripstoneClusterConfiguration> {
    public DripstoneClusterFeatureMixin(Codec<DripstoneClusterConfiguration> codec) {
        super(codec);
    }

    @WrapOperation(
            method = "placeColumn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/DripstoneUtils;growPointedDripstone(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;IZ)V"
            )
    )
    private void worldgenflexiblifier$placeColumnGrow(LevelAccessor level, BlockPos pos, Direction direction, int height, boolean mergeTip, Operation<Void> operation, WorldGenLevel level2, RandomSource random, BlockPos pos2, int x, int z, float wetness, double chance, int height2, float density, DripstoneClusterConfiguration config) {
        DripstoneClusterAlternateData data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data == null || data.isDefault()) {
            operation.call(level, pos, direction, height, mergeTip);
        } else {
            data.growPointedDripstone(level, pos, direction, height, mergeTip);
        }
    }

    @WrapOperation(
            method = "placeColumn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/DripstoneClusterFeature;replaceBlocksWithDripstoneBlocks(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/core/BlockPos;ILnet/minecraft/core/Direction;)V"
            )
    )
    private void worldgenflexiblifier$placeColumnReplace(DripstoneClusterFeature feature, WorldGenLevel level, BlockPos pos, int thickness, Direction direction, Operation<Void> operation, WorldGenLevel level2, RandomSource random, BlockPos pos2, int x, int z, float wetness, double chance, int height, float density, DripstoneClusterConfiguration config) {
        DripstoneClusterAlternateData data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data == null || data.isDefault()) {
            operation.call(feature, level, pos, thickness, direction);
        } else {
            data.replaceBlocksWithDripstoneBlocks(level, pos, thickness, direction);
        }
    }
}
