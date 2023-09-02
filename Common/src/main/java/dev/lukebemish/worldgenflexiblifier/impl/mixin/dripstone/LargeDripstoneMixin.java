package dev.lukebemish.worldgenflexiblifier.impl.mixin.dripstone;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.DripstoneClusterAlternateData;
import dev.lukebemish.worldgenflexiblifier.impl.dripstone.HasDripstoneData;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(targets = "net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature$LargeDripstone")
public class LargeDripstoneMixin implements HasDripstoneData {
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
        method = "placeBlocks",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/level/block/Blocks;DRIPSTONE_BLOCK:Lnet/minecraft/world/level/block/Block;",
            opcode = Opcodes.GETSTATIC
        )
    )
    private Block worldgenflexiblifier$wrapDripstoneBlock(Block original) {
        var data = worldgenflexiblifier$getDripstoneData();
        if (data != null && !data.isDefault()) {
            return data.base();
        }
        return original;
    }
}
