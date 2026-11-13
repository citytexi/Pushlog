package com.citytexi.pushlog.core.designsystem.theme.shadow

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/** 그림자 한 겹. 값은 `docs/architecture/design-system.md`. */
@Immutable
data class PushlogShadowToken(
    val offsetX: Dp,
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val color: Color,
)
