package dev.lukebemish.worldgenflexiblifier.impl.oreveins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record NoiseSettingsAlternateData(List<OreVein> extraOreVeins) {
    public static final Codec<NoiseSettingsAlternateData> CODEC = RecordCodecBuilder.create(i -> i.group(
            OreVein.CODEC.listOf().optionalFieldOf("extra_ore_veins", List.of()).forGetter(NoiseSettingsAlternateData::extraOreVeins)
    ).apply(i, NoiseSettingsAlternateData::new));
    public static final NoiseSettingsAlternateData DEFAULT = new NoiseSettingsAlternateData(List.of());
}
