package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** field 라벨 굵기. */
enum class PushlogFieldLabelWeight {
    REGULAR,
    MEDIUM,
    BOLD,
    ;

    internal val textStyle: TextStyle
        @Composable get() {
            val typography = PushlogTheme.typography
            return when (this) {
                REGULAR -> typography.t5Regular
                MEDIUM -> typography.t5Medium
                BOLD -> typography.t5Bold
            }
        }
}
