package dev.lukebemish.worldgenflexiblifier.impl.mixin.oreveins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.HasNoiseSettingsData;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.NoiseSettingsAlternateData;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(NoiseGeneratorSettings.class)
public class NoiseGeneratorSettingsMixin implements HasNoiseSettingsData {
    @Unique
    private NoiseSettingsAlternateData worldgenflexiblifier$noiseSettingsData = null;

    @Override
    public void worldgenflexiblifier$setNoiseSettingsData(NoiseSettingsAlternateData alt) {
        this.worldgenflexiblifier$noiseSettingsData = alt;
    }

    @Override
    public @Nullable NoiseSettingsAlternateData worldgenflexiblifier$getNoiseSettingsData() {
        return Objects.requireNonNull(worldgenflexiblifier$noiseSettingsData);
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
            )
    )
    private static Codec<NoiseGeneratorSettings> worldgenflexiblifier$wrapCodecSet(Codec<NoiseGeneratorSettings> originalCodec) {
        return Codec.pair(originalCodec, NoiseSettingsAlternateData.CODEC).xmap(p -> {
            var data = p.getFirst();
            ((HasNoiseSettingsData) (Object) data).worldgenflexiblifier$setNoiseSettingsData(p.getSecond());
            return data;
        }, data -> {
            NoiseSettingsAlternateData alt = ((HasNoiseSettingsData) (Object) data).worldgenflexiblifier$getNoiseSettingsData();
            return alt == null ? Pair.of(data, NoiseSettingsAlternateData.DEFAULT) : Pair.of(data, alt);
        });
    }
}
