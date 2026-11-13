package com.citytexi.pushlog.core.designsystem.theme.shadow

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset

/** 그림자 토큰을 `dropShadow`로 그린다. CSS blur보다 약간 퍼진다(`docs/architecture/design-system.md`). */
fun Modifier.pushlogShadow(
    token: PushlogShadowToken,
    shape: Shape,
): Modifier =
    dropShadow(
        shape,
        Shadow(
            radius = token.blur,
            spread = token.spread,
            offset = DpOffset(token.offsetX, token.offsetY),
            color = token.color,
        ),
    )
