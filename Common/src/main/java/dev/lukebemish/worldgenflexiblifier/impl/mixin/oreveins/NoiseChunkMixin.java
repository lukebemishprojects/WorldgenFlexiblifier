package dev.lukebemish.worldgenflexiblifier.impl.mixin.oreveins;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.HasOreVeins;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NoiseChunk.class)
public abstract class NoiseChunkMixin {
    @Shadow
    abstract protected DensityFunction wrap(DensityFunction densityFunction);

    @ModifyReceiver(
            method = "<init>",
            at = @At(
                    value = "worldgenflexiblifier:INVOKE",
                    target = "Lcom/google/common/collect/ImmutableList$Builder;build()Lcom/google/common/collect/ImmutableList;"
            )
    )
    private ImmutableList.Builder<NoiseChunk.BlockStateFiller> worldgenflexiblifier$wrapFillerList(ImmutableList.Builder<NoiseChunk.BlockStateFiller> original, int i, RandomState randomState, int j, int k, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blender) {
        @SuppressWarnings("DataFlowIssue") var veins = ((HasOreVeins) (Object) randomState).worldgenflexiblifier$getOreVeins();
        if (veins != null) {
            for (var oreVein : veins) {
                var mapped = oreVein.mapAll(this::wrap);
                original.add(mapped.create(randomState.oreRandom()));
            }
        }
        return original;
    }
}
