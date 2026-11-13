package com.citytexi.pushlog.core.designsystem.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** 테두리·구분선 용도 색. SEED `$color.stroke.*`. */
@Immutable
data class PushlogStrokeColors(
    val brandSolid: Color,
    val brandWeak: Color,
    val criticalSolid: Color,
    val criticalWeak: Color,
    val focusRing: Color,
    val informativeSolid: Color,
    val informativeWeak: Color,
    val neutralContrast: Color,
    val neutralMuted: Color,
    val neutralSolid: Color,
    val neutralSubtle: Color,
    val neutralWeak: Color,
    val positiveSolid: Color,
    val positiveWeak: Color,
    val warningSolid: Color,
    val warningWeak: Color,
)
