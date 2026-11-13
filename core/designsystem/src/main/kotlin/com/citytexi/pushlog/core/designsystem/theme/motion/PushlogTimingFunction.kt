package com.citytexi.pushlog.core.designsystem.theme.motion

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable

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
