package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.ui.text.TextStyle

/** 체크박스 라벨의 글자 굵기. */
enum class PushlogCheckboxWeight {
    REGULAR,
    BOLD,
    ;

    internal fun labelStyle(metrics: CheckmarkMetrics): TextStyle =
        when (this) {
            REGULAR -> metrics.regularLabelStyle
            BOLD -> metrics.boldLabelStyle
        }
}
