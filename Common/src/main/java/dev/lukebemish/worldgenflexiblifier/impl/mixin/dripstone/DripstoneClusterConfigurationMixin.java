package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.DripstoneClusterAlternateData;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.DripstoneClusterHasData;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(DripstoneClusterConfiguration.class)
public class DripstoneClusterConfigurationMixin implements DripstoneClusterHasData {


    @Unique
    private DripstoneClusterAlternateData worldgenflexiblifier$alternativeDripstoneData = null;

    @Override
    public void worldgenflexiblifier$setAlternativeDripstoneData(DripstoneClusterAlternateData alt) {
        this.worldgenflexiblifier$alternativeDripstoneData = alt;
    }

    @Override
    public @NotNull DripstoneClusterAlternateData worldgenflexiblifier$getAlternativeDripstoneData() {
        return Objects.requireNonNull(worldgenflexiblifier$alternativeDripstoneData);
    }

    @ModifyExpressionValue(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
            )
    )
    private static Codec<DripstoneClusterConfiguration> worldgenflexiblifier$wrapCodecSet(Codec<DripstoneClusterConfiguration> originalCodec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<DripstoneClusterConfiguration, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<DripstoneClusterConfiguration, T>> result = originalCodec.decode(ops, input);
                DataResult<DripstoneClusterAlternateData> dataParsed = DripstoneClusterAlternateData.CODEC.parse(ops, input);
                return result.flatMap(p -> {
                    DripstoneClusterConfiguration config = p.getFirst();
                    if (dataParsed.result().isPresent()) {
                        var data = dataParsed.result().get();
                        ((DripstoneClusterHasData) config).worldgenflexiblifier$setAlternativeDripstoneData(data);
                        return DataResult.success(Pair.of(config, p.getSecond()));
                    }
                    return DataResult.error(() -> dataParsed.error().get().message());
                });
            }

            @Override
            public <T> DataResult<T> encode(DripstoneClusterConfiguration input, DynamicOps<T> ops, T prefix) {
                DataResult<T> result = originalCodec.encode(input, ops, prefix);
                return result.flatMap(t -> {
                    DripstoneClusterAlternateData data = ((DripstoneClusterHasData) input).worldgenflexiblifier$getAlternativeDripstoneData();
                    return DripstoneClusterAlternateData.CODEC.encode(data, ops, t);
                });
            }
        };
    }
}
