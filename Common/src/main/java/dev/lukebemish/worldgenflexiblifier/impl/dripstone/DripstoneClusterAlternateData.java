package dev.lukebemish.worldgenflexiblifier.impl.dripstone;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.worldgenflexiblifier.impl.utils.Codecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public record DripstoneClusterAlternateData(Block base, PointedDripstoneCreator pointed, TagKey<Block> replaceableTag) {
    public static final Codec<DripstoneClusterAlternateData> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("base_block", Blocks.DRIPSTONE_BLOCK).forGetter(DripstoneClusterAlternateData::base),
            Codec.either(BuiltInRegistries.BLOCK.byNameCodec().xmap(PointedDripstoneCreator.ByBlock::new, PointedDripstoneCreator.ByBlock::block), PointedDripstoneCreator.ByMap.CODEC)
                    .<PointedDripstoneCreator>flatXmap(e -> DataResult.success(e.map(Function.identity(), Function.identity())), creator -> {
                        if (creator instanceof PointedDripstoneCreator.ByBlock byBlock) {
                            return DataResult.success(Either.left(byBlock));
                        } else if (creator instanceof PointedDripstoneCreator.ByMap byMap) {
                            return DataResult.success(Either.right(byMap));
                        } else {
                            return DataResult.error(() -> "Pointed dripstone creator is not a serializable type!");
                        }
                    })
                    .optionalFieldOf("pointed_block", PointedDripstoneCreator.DEFAULT).forGetter(DripstoneClusterAlternateData::pointed),
            TagKey.codec(Registries.BLOCK).optionalFieldOf("replaceable_tag", BlockTags.DRIPSTONE_REPLACEABLE).forGetter(DripstoneClusterAlternateData::replaceableTag)
    ).apply(i, DripstoneClusterAlternateData::new));

    public boolean isDefault() {
        return base == Blocks.DRIPSTONE_BLOCK && pointed == PointedDripstoneCreator.DEFAULT && replaceableTag == BlockTags.DRIPSTONE_REPLACEABLE;
    }

    private boolean isDripstoneBase(BlockState state) {
        return state.is(base()) || state.is(replaceableTag);
    }

    private boolean isDripstoneBaseOrLava(BlockState state) {
        return state.is(Blocks.LAVA) || isDripstoneBase(state);
    }

    private BlockState createPointedDripstone(Direction direction, DripstoneThickness dripstoneThickness) {
        return pointed().create(dripstoneThickness, direction);
    }

    public void growPointedDripstone(LevelAccessor level, BlockPos pos, Direction direction, int height, boolean mergeTip) {
        if (isDripstoneBase(level.getBlockState(pos.relative(direction.getOpposite())))) {
            BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
            buildBaseToTipColumn(direction, height, mergeTip, (blockState) -> {
                if (pointed().is(blockState)) {
                    blockState = blockState.setValue(PointedDripstoneBlock.WATERLOGGED, level.isWaterAt(mutableBlockPos));
                }

                level.setBlock(mutableBlockPos, blockState, 2);
                mutableBlockPos.move(direction);
            });
        }
    }

    private void buildBaseToTipColumn(Direction direction, int height, boolean mergeTip, Consumer<BlockState> blockSetter) {
        for (int remaining = height; remaining > 0; remaining--) {
            if (remaining == 1) {
                blockSetter.accept(createPointedDripstone(direction, mergeTip ? DripstoneThickness.TIP_MERGE : DripstoneThickness.TIP));
            } else if (remaining == 2) {
                blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.FRUSTUM));
            } else if (remaining == height) {
                blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.BASE));
            } else {
                blockSetter.accept(createPointedDripstone(direction, DripstoneThickness.MIDDLE));
            }
        }
    }

    public void replaceBlocksWithDripstoneBlocks(WorldGenLevel level, BlockPos pos, int thickness, Direction direction) {
        BlockPos.MutableBlockPos mutableBlockPos = pos.mutable();
        for(int i = 0; i < thickness; ++i) {
            BlockState blockState = level.getBlockState(pos);
            if (blockState.is(replaceableTag())) {
                level.setBlock(pos, base().defaultBlockState(), 2);
                return;
            }
            mutableBlockPos.move(direction);
        }

    }

    public interface PointedDripstoneCreator {
        BlockState create(DripstoneThickness dripstoneThickness, Direction direction);
        boolean is(BlockState state);

        record ByBlock(Block block) implements PointedDripstoneCreator {

            @Override
            public BlockState create(DripstoneThickness dripstoneThickness, Direction direction) {
                return block().defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, direction).setValue(PointedDripstoneBlock.THICKNESS, dripstoneThickness);
            }

            @Override
            public boolean is(BlockState state) {
                return state.is(block());
            }
        }

        record ByMap(Map<DripstoneThickness, BlockState> up, Map<DripstoneThickness, BlockState> down) implements PointedDripstoneCreator {
            public static final Codec<ByMap> CODEC = RecordCodecBuilder.create(i -> i.group(
                    Codec.unboundedMap(StringRepresentable.fromEnum(DripstoneThickness::values), Codecs.BLOCK_STATE).flatXmap(ByMap::verify, DataResult::success).fieldOf("up").forGetter(ByMap::up),
                    Codec.unboundedMap(StringRepresentable.fromEnum(DripstoneThickness::values), Codecs.BLOCK_STATE).flatXmap(ByMap::verify, DataResult::success).fieldOf("down").forGetter(ByMap::down)
            ).apply(i, ByMap::new));

            @Override
            public BlockState create(DripstoneThickness dripstoneThickness, Direction direction) {
                if (direction == Direction.DOWN) {
                    return down().get(dripstoneThickness);
                }
                return up().get(dripstoneThickness);
            }

            private static DataResult<Map<DripstoneThickness, BlockState>> verify(Map<DripstoneThickness, BlockState> map) {
                for (DripstoneThickness thickness : DripstoneThickness.values()) {
                    if (!map.containsKey(thickness)) {
                        return DataResult.error(() -> "Missing thickness " + thickness + " in map!");
                    }
                }
                return DataResult.success(map);
            }

            @Override
            public boolean is(BlockState state) {
                return up().containsValue(state) || down().containsValue(state);
            }
        }

        PointedDripstoneCreator DEFAULT = new ByBlock(Blocks.POINTED_DRIPSTONE);
    }
}