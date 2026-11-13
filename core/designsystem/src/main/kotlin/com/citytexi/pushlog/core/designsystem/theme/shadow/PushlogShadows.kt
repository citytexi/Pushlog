package com.citytexi.pushlog.core.designsystem.theme.shadow

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
