package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.HasDripstoneData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature;
import net.minecraft.world.level.levelgen.feature.configurations.LargeDripstoneConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(LargeDripstoneFeature.class)
public class LargeDripstoneFeatureMixin {
    @WrapOperation(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/Column;scan(Lnet/minecraft/world/level/LevelSimulatedReader;Lnet/minecraft/core/BlockPos;ILjava/util/function/Predicate;Ljava/util/function/Predicate;)Ljava/util/Optional;"
        )
    )
    private Optional<Column> worldgenflexiblifier$columnScanModify(LevelSimulatedReader level, BlockPos pos, int maxDistance, Predicate<BlockState> columnPredicate, Predicate<BlockState> tipPredicate, Operation<Optional<Column>> operation, FeaturePlaceContext<LargeDripstoneConfiguration> context) {
        var config = context.config();
        var data = ((HasDripstoneData) config).worldgenflexiblifier$getDripstoneData();
        if (data != null && !data.isDefault()) {
            Predicate<BlockState> newPredicate = data::isDripstoneBaseOrLava;
            return operation.call(level, pos, maxDistance, columnPredicate, newPredicate);
        }
        return operation.call(level, pos, maxDistance, columnPredicate, tipPredicate);
    }

    @WrapWithCondition(
        method = "place",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/feature/LargeDripstoneFeature$LargeDripstone;placeBlocks(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/level/levelgen/feature/LargeDripstoneFeature$WindOffsetter;)V"
        ),
        expect = 2
    )
    private boolean worldgenflexiblifier$wrapLargeDripstoneCreation(@Coerce Object largeDripstone, WorldGenLevel level, RandomSource random, @Coerce Object windOffsetter, FeaturePlaceContext<LargeDripstoneConfiguration> context) {
        var data = ((HasDripstoneData) context.config()).worldgenflexiblifier$getDripstoneData();
        ((HasDripstoneData) largeDripstone).worldgenflexiblifier$setDripstoneData(data);
        return true;
    }
}
