package dev.lukebemish.worldgenflexiblifier.impl.oreveins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.worldgenflexiblifier.impl.utils.Codecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public record OreVein(DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, Placement placement, Type type) {
    public static final Codec<OreVein> CODEC = RecordCodecBuilder.create(i -> i.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_toggle").forGetter(OreVein::veinToggle),
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_ridged").forGetter(OreVein::veinRidged),
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_gap").forGetter(OreVein::veinGap),
            Placement.CODEC.fieldOf("placement").forGetter(OreVein::placement),
            Type.CODEC.fieldOf("type").forGetter(OreVein::type)
    ).apply(i, OreVein::new));

    public OreVein mapAll(DensityFunction.Visitor visitor) {
        return new OreVein(veinToggle.mapAll(visitor), veinRidged.mapAll(visitor), veinGap.mapAll(visitor), placement, type);
    }

    public NoiseChunk.BlockStateFiller create(PositionalRandomFactory positionalRandomFactory) {
        return context -> {
            int y = context.blockY();
            int fromTop = placement.maxY - y;
            int fromBottom = y - placement.minY;
            if (fromTop >= 0 && fromBottom >= 0) {
                var veininess = veinToggle.compute(context);
                int minDistance = Math.min(fromTop, fromBottom);
                var edgeRoundoff = Mth.clampedMap(minDistance, 0.0, placement.edgeRoundoffBegin, -placement.maxEdgeRoundoff, 0.0);
                if (edgeRoundoff + veininess >= placement.veininessThreshold) {
                    var randomSource = positionalRandomFactory.at(context.blockX(), y, context.blockZ());
                    if (randomSource.nextFloat() <= placement.veinSolidness && veinRidged.compute(context) < 0) {
                        var richness = Mth.clampedMap(veininess, placement.veininessThreshold, placement.maxRichnessThreshold, placement.minRichness, placement.maxRichness);
                        if (randomSource.nextFloat() < richness && veinGap.compute(context) > placement.skipOreIfBelow) {
                            return randomSource.nextFloat() < placement.chanceOfRawOre ? type.rawOre : type.ore;
                        }
                        return type.filler;
                    }
                }
            }
            return null;
        };
    }

    public record Placement(float veininessThreshold, int edgeRoundoffBegin, double maxEdgeRoundoff, float veinSolidness,
                            float minRichness, float maxRichness, float maxRichnessThreshold, float chanceOfRawOre, float skipOreIfBelow,
                            int minY, int maxY) {
        public static final Codec<Placement> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.optionalFieldOf("veininess_threshold", 0.4f).forGetter(Placement::veininessThreshold),
                Codec.INT.optionalFieldOf("edge_roundoff_begin", 20).forGetter(Placement::edgeRoundoffBegin),
                Codec.DOUBLE.optionalFieldOf("max_edge_roundoff", 0.2).forGetter(Placement::maxEdgeRoundoff),
                Codec.FLOAT.optionalFieldOf("vein_solidness", 0.7f).forGetter(Placement::veinSolidness),
                Codec.FLOAT.optionalFieldOf("min_richness", 0.1f).forGetter(Placement::minRichness),
                Codec.FLOAT.optionalFieldOf("max_richness", 0.3f).forGetter(Placement::maxRichness),
                Codec.FLOAT.optionalFieldOf("max_richness_threshold", 0.6f).forGetter(Placement::maxRichnessThreshold),
                Codec.FLOAT.optionalFieldOf("chance_of_raw_ore", 0.02f).forGetter(Placement::chanceOfRawOre),
                Codec.FLOAT.optionalFieldOf("skip_ore_if_below", -0.3f).forGetter(Placement::skipOreIfBelow),
                Codec.INT.fieldOf("min_y").forGetter(Placement::minY),
                Codec.INT.fieldOf("max_y").forGetter(Placement::maxY)
        ).apply(i, Placement::new));
    }

    public record Type(BlockState ore, BlockState rawOre, BlockState filler) {
        public static final Codec<Type> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codecs.BLOCK_STATE.fieldOf("ore").forGetter(Type::ore),
                Codecs.BLOCK_STATE.fieldOf("raw_ore").forGetter(Type::rawOre),
                Codecs.BLOCK_STATE.fieldOf("filler").forGetter(Type::filler)
        ).apply(i, Type::new));
    }
}
