package com.citytexi.pushlog.core.designsystem.theme.dimension

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 간격 토큰. `xNHalf`는 SEED의 `xN_5`. 값은 `docs/architecture/design-system.md`. */
@Immutable
data class PushlogDimension(
    val x0Half: Dp,
    val x1: Dp,
    val x1Half: Dp,
    val x2: Dp,
    val x2Half: Dp,
    val x3: Dp,
    val x3Half: Dp,
    val x4: Dp,
    val x4Half: Dp,
    val x5: Dp,
    val x6: Dp,
    val x7: Dp,
    val x8: Dp,
    val x9: Dp,
    val x10: Dp,
    val x12: Dp,
    val x13: Dp,
    val x14: Dp,
    val x16: Dp,
    val spacingX: PushlogSpacingX,
    val spacingY: PushlogSpacingY,
)

@Immutable
data class PushlogSpacingX(
    val globalGutter: Dp,
    val betweenChips: Dp,
)

@Immutable
data class PushlogSpacingY(
    val betweenText: Dp,
    val componentDefault: Dp,
    val navToTitle: Dp,
    val screenBottom: Dp,
)

/** 모서리 토큰. `rNHalf`는 SEED의 `rN_5`. */
@Immutable
data class PushlogRadius(
    val r0Half: Dp,
    val r1: Dp,
    val r1Half: Dp,
    val r2: Dp,
    val r2Half: Dp,
    val r3: Dp,
    val r3Half: Dp,
    val r4: Dp,
    val r5: Dp,
    val r6: Dp,
    val full: Shape,
)

internal val DefaultPushlogDimension =
    PushlogDimension(
        x0Half = 2.dp,
        x1 = 4.dp,
        x1Half = 6.dp,
        x2 = 8.dp,
        x2Half = 10.dp,
        x3 = 12.dp,
        x3Half = 14.dp,
        x4 = 16.dp,
        x4Half = 18.dp,
        x5 = 20.dp,
        x6 = 24.dp,
        x7 = 28.dp,
        x8 = 32.dp,
        x9 = 36.dp,
        x10 = 40.dp,
        x12 = 48.dp,
        x13 = 52.dp,
        x14 = 56.dp,
        x16 = 64.dp,
        spacingX =
            PushlogSpacingX(
                globalGutter = 16.dp,
                betweenChips = 8.dp,
            ),
        spacingY =
            PushlogSpacingY(
                betweenText = 6.dp,
                componentDefault = 12.dp,
                navToTitle = 20.dp,
                screenBottom = 56.dp,
            ),
    )

internal val DefaultPushlogRadius =
    PushlogRadius(
        r0Half = 2.dp,
        r1 = 4.dp,
        r1Half = 6.dp,
        r2 = 8.dp,
        r2Half = 10.dp,
        r3 = 12.dp,
        r3Half = 14.dp,
        r4 = 16.dp,
        r5 = 20.dp,
        r6 = 24.dp,
        full = RoundedCornerShape(percent = 50),
    )
