package com.citytexi.pushlog.core.designsystem.component.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

enum class PushlogChipVariant {
    /** 기본 스타일. 테두리 없이 옅은 면으로 구분한다. */
    SOLID,

    /** 선택 목록에서 주목도를 낮출 때 쓴다. 테두리로 구분한다. */
    OUTLINE_WEAK,
    ;

    @Composable
    internal fun surfaceColor(
        selected: Boolean,
        pressed: Boolean,
    ): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            SOLID ->
                when {
                    selected -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
                    else -> if (pressed) bg.neutralWeakAlphaPressed else bg.neutralWeakAlpha
                }
            OUTLINE_WEAK ->
                when {
                    selected -> if (pressed) bg.neutralWeakPressed else bg.neutralWeak
                    else -> if (pressed) bg.transparentPressed else bg.transparent
                }
        }
    }

    @Composable
    internal fun contentColor(selected: Boolean): Color {
        val fg = PushlogTheme.color.fg
        return if (this == SOLID && selected) fg.onNeutralSolid else fg.neutral
    }

    @Composable
    internal fun borderColor(selected: Boolean): Color {
        val stroke = PushlogTheme.color.stroke
        return when {
            this == SOLID -> Color.Transparent
            selected -> stroke.neutralContrast
            else -> stroke.neutralWeak
        }
    }
}
