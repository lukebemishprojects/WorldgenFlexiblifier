package dev.lukebemish.worldgenflexiblifier.impl.mixin.oreveins;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.HasNoiseSettingsData;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.HasOreVeins;
import dev.lukebemish.worldgenflexiblifier.impl.oreveins.OreVein;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RandomState.class)
public class RandomStateMixin implements HasOreVeins {
    @Unique
    private List<OreVein> worldgenflexiblifier$oreVeins = null;

    @Override
    public void worldgenflexiblifier$setOreVeins(List<OreVein> oreVeins) {
        this.worldgenflexiblifier$oreVeins = oreVeins;
    }

    @Override
    public @Nullable List<OreVein> worldgenflexiblifier$getOreVeins() {
        return this.worldgenflexiblifier$oreVeins;
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN")
    )
    private void worldgenflexiblifier$initOreVeins(NoiseGeneratorSettings noiseGeneratorSettings, HolderGetter<NormalNoise.NoiseParameters> holderGetter, final long l, CallbackInfo ci, @Share("visitor") LocalRef<DensityFunction.Visitor> visitorRef) {
        @SuppressWarnings("DataFlowIssue") var data = ((HasNoiseSettingsData) (Object) noiseGeneratorSettings).worldgenflexiblifier$getNoiseSettingsData();
        if (data != null) {
            ImmutableList.Builder<OreVein> veins = ImmutableList.builder();
            for (var oreVein : data.extraOreVeins()) {
                veins.add(oreVein.mapAll(visitorRef.get()));
            }
            this.worldgenflexiblifier$setOreVeins(veins.build());
        }
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "worldgenflexiblifier:INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
            )
    )
    private NoiseRouter worldgenflexiblifier$captureVisitor(NoiseRouter router, DensityFunction.Visitor visitor, Operation<NoiseRouter> operation, @Share("visitor") LocalRef<DensityFunction.Visitor> visitorRef) {
        visitorRef.set(visitor);
        return operation.call(router, visitor);
    }
}
