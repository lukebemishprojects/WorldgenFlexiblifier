package dev.lukebemish.worldgenflexiblifier.impl.oreveins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.worldgenflexiblifier.impl.utils.Codecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record OreVeinConfiguration(DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, Placement placement, Material material, TagKey<Block> replace) implements FeatureConfiguration {
    public static final Codec<OreVeinConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_toggle").forGetter(OreVeinConfiguration::veinToggle),
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_ridged").forGetter(OreVeinConfiguration::veinRidged),
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("vein_gap").forGetter(OreVeinConfiguration::veinGap),
            Placement.CODEC.fieldOf("placement").forGetter(OreVeinConfiguration::placement),
            Material.CODEC.fieldOf("material").forGetter(OreVeinConfiguration::material),
            TagKey.codec(Registries.BLOCK).fieldOf("replace").forGetter(OreVeinConfiguration::replace)
    ).apply(i, OreVeinConfiguration::new));

    public OreVeinConfiguration mapAll(DensityFunction.Visitor visitor) {
        return new OreVeinConfiguration(veinToggle.mapAll(visitor), veinRidged.mapAll(visitor), veinGap.mapAll(visitor), placement, material, replace);
    }

    public BlockState create(PositionalRandomFactory positionalRandomFactory, BlockPos pos, DensityFunction.FunctionContext context) {
        int y = pos.getY();
        int fromTop = placement.maxY - y;
        int fromBottom = y - placement.minY;
        if (fromTop >= 0 && fromBottom >= 0) {
            var veininess = veinToggle.compute(context);
            int minDistance = Math.min(fromTop, fromBottom);
            var edgeRoundoff = Mth.clampedMap(minDistance, 0.0, placement.edgeRoundoffBegin, -placement.maxEdgeRoundoff, 0.0);
            if (edgeRoundoff + veininess >= placement.veininessThreshold) {
                var randomSource = positionalRandomFactory.at(pos.getX(), y, pos.getZ());
                if (randomSource.nextFloat() <= placement.veinSolidness && veinRidged.compute(context) < 0) {
                    var richness = Mth.clampedMap(veininess, placement.veininessThreshold, placement.maxRichnessThreshold, placement.minRichness, placement.maxRichness);
                    if (randomSource.nextFloat() < richness && veinGap.compute(context) > placement.skipOreIfBelow) {
                        return material.blocks.getRandom(randomSource).map(WeightedEntry.Wrapper::getData).orElse(material.filler);
                    }
                    return material.filler;
                }
            }
        }
        return null;
    }

    public record Placement(float veininessThreshold, int edgeRoundoffBegin, double maxEdgeRoundoff, float veinSolidness,
                            float minRichness, float maxRichness, float maxRichnessThreshold, float skipOreIfBelow,
                            int minY, int maxY) {
        public static final Codec<Placement> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.optionalFieldOf("veininess_threshold", 0.4f).forGetter(Placement::veininessThreshold),
                Codec.INT.optionalFieldOf("edge_roundoff_begin", 20).forGetter(Placement::edgeRoundoffBegin),
                Codec.DOUBLE.optionalFieldOf("max_edge_roundoff", 0.2).forGetter(Placement::maxEdgeRoundoff),
                Codec.FLOAT.optionalFieldOf("vein_solidness", 0.7f).forGetter(Placement::veinSolidness),
                Codec.FLOAT.optionalFieldOf("min_richness", 0.1f).forGetter(Placement::minRichness),
                Codec.FLOAT.optionalFieldOf("max_richness", 0.3f).forGetter(Placement::maxRichness),
                Codec.FLOAT.optionalFieldOf("max_richness_threshold", 0.6f).forGetter(Placement::maxRichnessThreshold),
                Codec.FLOAT.optionalFieldOf("skip_ore_if_below", -0.3f).forGetter(Placement::skipOreIfBelow),
                Codec.INT.fieldOf("min_y").forGetter(Placement::minY),
                Codec.INT.fieldOf("max_y").forGetter(Placement::maxY)
        ).apply(i, Placement::new));
    }

    public record Material(WeightedRandomList<WeightedEntry.Wrapper<BlockState>> blocks, BlockState filler) {
        public static final Codec<Material> CODEC = RecordCodecBuilder.create(i -> i.group(
                WeightedRandomList.codec(WeightedEntry.Wrapper.codec(Codecs.BLOCK_STATE)).fieldOf("blocks").forGetter(Material::blocks),
                Codecs.BLOCK_STATE.fieldOf("filler").forGetter(Material::filler)
        ).apply(i, Material::new));
    }
}
