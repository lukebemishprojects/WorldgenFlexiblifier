package dev.lukebemish.worldgenflexiblifier.impl.utils;

import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode("INVOKE")
public class UniversalBeforeInvoke extends BeforeInvoke {
    public UniversalBeforeInvoke(InjectionPointData data) {
        super(data);
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public RestrictTargetLevel getTargetRestriction(IInjectionPointContext context) {
        return RestrictTargetLevel.CONSTRUCTORS_AFTER_DELEGATE;
    }
}
