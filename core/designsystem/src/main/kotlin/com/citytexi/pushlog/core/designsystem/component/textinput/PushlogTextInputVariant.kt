package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED text-input 여러 줄 최소 높이. 토큰이 없다.
private val MULTILINE_MIN_HEIGHT = 94.dp

enum class PushlogTextInputVariant {
    /** 사방 테두리 상자. */
    OUTLINE,

    /** 아래쪽 선만 있는 입력. */
    UNDERLINE,
    ;

    @Composable
    internal fun metrics(multiline: Boolean): TextInputMetrics {
        val dimension = PushlogTheme.dimension
        val typography = PushlogTheme.typography
        return when (this) {
            OUTLINE ->
                TextInputMetrics(
                    minHeight = if (multiline) MULTILINE_MIN_HEIGHT else dimension.x13,
                    paddingX = dimension.x4,
                    paddingY = if (multiline) dimension.x3Half else 0.dp,
                    gap = dimension.x2Half,
                    cornerRadius = PushlogTheme.radius.r3,
                    textStyle = typography.t5Regular,
                    iconSize = dimension.x5,
                )
            UNDERLINE ->
                TextInputMetrics(
                    minHeight = if (multiline) MULTILINE_MIN_HEIGHT else dimension.x10,
                    paddingX = 0.dp,
                    paddingY = if (multiline) dimension.x3Half else dimension.x2,
                    gap = dimension.x2Half,
                    cornerRadius = 0.dp,
                    textStyle = typography.t6Regular,
                    iconSize = dimension.x6,
                )
        }
    }

    @Composable
    internal fun containerColor(
        enabled: Boolean,
        readOnly: Boolean,
    ): Color =
        when (this) {
            OUTLINE -> if (!enabled || readOnly) PushlogTheme.color.bg.disabled else Color.Transparent
            UNDERLINE -> Color.Transparent
        }

    @Composable
    internal fun valueColor(
        enabled: Boolean,
        readOnly: Boolean,
    ): Color = textColor(enabled, readOnly, PushlogTheme.color.fg.neutral)

    @Composable
    internal fun placeholderColor(
        enabled: Boolean,
        readOnly: Boolean,
    ): Color = textColor(enabled, readOnly, PushlogTheme.color.fg.placeholder)

    @Composable
    private fun textColor(
        enabled: Boolean,
        readOnly: Boolean,
        normal: Color,
    ): Color {
        val fg = PushlogTheme.color.fg
        return when {
            !enabled -> fg.disabled
            this == UNDERLINE && readOnly -> fg.neutralMuted
            else -> normal
        }
    }
}
