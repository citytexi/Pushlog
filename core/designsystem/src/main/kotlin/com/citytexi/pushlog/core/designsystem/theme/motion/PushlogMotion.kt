package com.citytexi.pushlog.core.designsystem.theme.motion

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable

/** 모션 토큰. 시간은 밀리초. 값은 `docs/architecture/design-system.md`. */
@Immutable
data class PushlogMotion(
    val duration: PushlogDuration,
    val timingFunction: PushlogTimingFunction,
)

@Immutable
data class PushlogDuration(
    val d1: Int,
    val d2: Int,
    val d3: Int,
    val d4: Int,
    val d5: Int,
    val d6: Int,
    val colorTransition: Int,
    val pressedScale: Int,
)

@Immutable
data class PushlogTimingFunction(
    val linear: Easing,
    val easing: Easing,
    val enter: Easing,
    val exit: Easing,
    val enterExpressive: Easing,
    val exitExpressive: Easing,
    val pressedScale: Easing,
)

internal val DefaultPushlogMotion =
    PushlogMotion(
        duration =
            PushlogDuration(
                d1 = 50,
                d2 = 100,
                d3 = 150,
                d4 = 200,
                d5 = 250,
                d6 = 300,
                colorTransition = 150,
                pressedScale = 150,
            ),
        timingFunction =
            PushlogTimingFunction(
                linear = CubicBezierEasing(0f, 0f, 1f, 1f),
                easing = CubicBezierEasing(0.35f, 0f, 0.35f, 1f),
                enter = CubicBezierEasing(0f, 0f, 0.15f, 1f),
                exit = CubicBezierEasing(0.35f, 0f, 1f, 1f),
                enterExpressive = CubicBezierEasing(0.03f, 0.4f, 0.1f, 1f),
                exitExpressive = CubicBezierEasing(0.35f, 0f, 0.95f, 0.55f),
                pressedScale = CubicBezierEasing(0f, 0f, 0.15f, 1f),
            ),
    )
