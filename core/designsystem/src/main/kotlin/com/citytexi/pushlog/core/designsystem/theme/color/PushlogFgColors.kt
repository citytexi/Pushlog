package com.citytexi.pushlog.core.designsystem.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** 글자·아이콘 용도 색. SEED `$color.fg.*`. */
@Immutable
data class PushlogFgColors(
    val brand: Color,
    val brandContrast: Color,
    val critical: Color,
    val criticalContrast: Color,
    val disabled: Color,
    val informative: Color,
    val informativeContrast: Color,
    val neutral: Color,
    val neutralInverted: Color,
    val neutralMuted: Color,
    val neutralSubtle: Color,
    val onBrandSolid: Color,
    val onCriticalSolid: Color,
    val onInformativeSolid: Color,
    val onNeutralSolid: Color,
    val onPositiveSolid: Color,
    val onWarningSolid: Color,
    val placeholder: Color,
    val positive: Color,
    val positiveContrast: Color,
    val warning: Color,
    val warningContrast: Color,
)
