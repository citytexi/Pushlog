package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.ui.text.TextStyle

/** 라디오 라벨의 글자 굵기. */
enum class PushlogRadioWeight {
    REGULAR,
    BOLD,
    ;

    internal fun labelStyle(metrics: RadiomarkMetrics): TextStyle =
        when (this) {
            REGULAR -> metrics.regularLabelStyle
            BOLD -> metrics.boldLabelStyle
        }
}
