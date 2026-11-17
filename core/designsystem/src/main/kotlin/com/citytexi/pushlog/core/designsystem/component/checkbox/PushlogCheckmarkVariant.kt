package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** 체크 마크 모양. 색은 `on`(체크 또는 일부 선택)과 [PushlogCheckmarkTone]에 따라 정한다. */
enum class PushlogCheckmarkVariant {
    /** 네모 상자. 반드시 골라야 하거나 사용자가 꼭 인지해야 하는 선택에 쓴다. */
    SQUARE,

    /** 상자 없이 체크만 그린다. 선택 사항이고 항목이 3개 이하일 때 쓴다. */
    GHOST,
    ;

    @Composable
    internal fun rootColor(
        tone: PushlogCheckmarkTone,
        on: Boolean,
        enabled: Boolean,
    ): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            SQUARE ->
                when {
                    !enabled -> bg.disabled
                    on -> tone.solid(pressed = false)
                    else -> bg.transparent
                }
            GHOST -> bg.transparent
        }
    }

    @Composable
    internal fun rootPressedColor(
        tone: PushlogCheckmarkTone,
        on: Boolean,
    ): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            SQUARE -> if (on) tone.solid(pressed = true) else bg.transparentPressed
            GHOST -> if (on) tone.ghostSelectedPressed() else bg.transparentPressed
        }
    }

    /** null이면 테두리를 그리지 않는다. */
    @Composable
    internal fun strokeColor(on: Boolean): Color? =
        when (this) {
            SQUARE -> if (on) null else PushlogTheme.color.stroke.neutralWeak
            GHOST -> null
        }

    /** null이면 아이콘을 그리지 않는다. */
    @Composable
    internal fun iconColor(
        tone: PushlogCheckmarkTone,
        on: Boolean,
        enabled: Boolean,
    ): Color? {
        val fg = PushlogTheme.color.fg
        return when (this) {
            SQUARE ->
                when {
                    !on -> null
                    !enabled -> fg.disabled
                    else -> tone.onSolid()
                }
            GHOST ->
                when {
                    !enabled -> fg.disabled
                    on -> tone.ghostSelectedIcon()
                    else -> fg.placeholder
                }
        }
    }

    internal fun iconSize(metrics: CheckmarkMetrics): Dp =
        when (this) {
            SQUARE -> metrics.squareIconSize
            GHOST -> metrics.ghostIconSize
        }
}
