/*
 * Copyright (C) 2023 Luke Bemish and contributors
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package dev.lukebemish.worldgenflexiblifier.impl.utils;

import dev.lukebemish.worldgenflexiblifier.impl.Constants;
import org.spongepowered.asm.mixin.injection.IInjectionPointContext;
import org.spongepowered.asm.mixin.injection.InjectionPoint;
import org.spongepowered.asm.mixin.injection.points.BeforeInvoke;
import org.spongepowered.asm.mixin.injection.struct.InjectionPointData;

@InjectionPoint.AtCode(UniversalBeforeInvoke.PARTIAL_AT_CODE)
public class UniversalBeforeInvoke extends BeforeInvoke {
    static final String PARTIAL_AT_CODE = "INVOKE";
    public static final String AT_CODE = Constants.MOD_ID + ":" + PARTIAL_AT_CODE;

    public UniversalBeforeInvoke(InjectionPointData data) {
        super(data);
    }

    @SuppressWarnings("RedundantMethodOverride")
    @Override
    public RestrictTargetLevel getTargetRestriction(IInjectionPointContext context) {
        return RestrictTargetLevel.CONSTRUCTORS_AFTER_DELEGATE;
    }
}
