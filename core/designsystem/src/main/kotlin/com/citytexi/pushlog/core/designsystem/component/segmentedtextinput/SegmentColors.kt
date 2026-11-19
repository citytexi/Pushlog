package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** 값 글자색과 placeholder·점·구분자 색. 읽기 전용은 활성과 같다(SEED segmented-text-input). */
@Immutable
internal data class SegmentColors(
    val value: Color,
    val decor: Color,
) {
    companion object {
        @Composable
        fun of(enabled: Boolean): SegmentColors {
            val fg = PushlogTheme.color.fg
            return if (enabled) SegmentColors(fg.neutral, fg.placeholder) else SegmentColors(fg.disabled, fg.disabled)
        }
    }
}
