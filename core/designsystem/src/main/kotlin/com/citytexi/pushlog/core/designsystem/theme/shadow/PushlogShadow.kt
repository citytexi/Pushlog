package com.citytexi.pushlog.core.designsystem.theme.shadow

import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

/** 그림자 한 겹. 값은 `docs/architecture/design-system.md`. */
@Immutable
data class PushlogShadowToken(
    val offsetX: Dp,
    val offsetY: Dp,
    val blur: Dp,
    val spread: Dp,
    val color: Color,
)

@Immutable
data class PushlogShadows(
    val s1: PushlogShadowToken,
    val s2: PushlogShadowToken,
    val s3: PushlogShadowToken,
)

internal val LightPushlogShadows =
    PushlogShadows(
        s1 = PushlogShadowToken(0.dp, 1.dp, 4.dp, 0.dp, Color(0x14000000)),
        s2 = PushlogShadowToken(0.dp, 2.dp, 10.dp, 0.dp, Color(0x1A000000)),
        s3 = PushlogShadowToken(0.dp, 4.dp, 16.dp, 0.dp, Color(0x1F000000)),
    )

internal val DarkPushlogShadows =
    PushlogShadows(
        s1 = PushlogShadowToken(0.dp, 1.dp, 4.dp, 0.dp, Color(0x80000000)),
        s2 = PushlogShadowToken(0.dp, 2.dp, 10.dp, 0.dp, Color(0xAD000000)),
        s3 = PushlogShadowToken(0.dp, 4.dp, 16.dp, 0.dp, Color(0xCC000000)),
    )

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
