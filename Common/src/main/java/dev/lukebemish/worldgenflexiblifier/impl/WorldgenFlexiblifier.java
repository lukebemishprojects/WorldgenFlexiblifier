package dev.lukebemish.worldgenflexiblifier.impl;

import dev.lukebemish.worldgenflexiblifier.impl.oreveins.OreVeinConfiguration;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.OreVeinFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class WorldgenFlexiblifier {
    public static void registerFeatures(BiConsumer<String, Supplier<Feature<?>>> consumer) {
        consumer.accept("ore_vein", () -> new OreVeinFeature(OreVeinConfiguration.CODEC));
    }
}
