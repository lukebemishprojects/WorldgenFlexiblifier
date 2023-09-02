package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.DripstoneClusterAlternateData;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.HasDripstoneData;
import net.minecraft.world.level.levelgen.feature.configurations.DripstoneClusterConfiguration;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(DripstoneClusterConfiguration.class)
public class DripstoneClusterConfigurationMixin implements HasDripstoneData {


    @Unique
    private DripstoneClusterAlternateData worldgenflexiblifier$alternativeDripstoneData = null;

    @Override
    public void worldgenflexiblifier$setDripstoneData(DripstoneClusterAlternateData alt) {
        this.worldgenflexiblifier$alternativeDripstoneData = alt;
    }

    @Override
    public @Nullable DripstoneClusterAlternateData worldgenflexiblifier$getDripstoneData() {
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
        return Codec.pair(originalCodec, DripstoneClusterAlternateData.CODEC).xmap(p -> {
            var data = p.getFirst();
            ((HasDripstoneData) data).worldgenflexiblifier$setDripstoneData(p.getSecond());
            return data;
        }, data -> {
            DripstoneClusterAlternateData alt = ((HasDripstoneData) data).worldgenflexiblifier$getDripstoneData();
            return alt == null ? Pair.of(data, DripstoneClusterAlternateData.DEFAULT) : Pair.of(data, alt);
        });
    }
}
