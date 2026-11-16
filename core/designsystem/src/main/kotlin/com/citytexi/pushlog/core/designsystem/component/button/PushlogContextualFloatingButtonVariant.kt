package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

enum class PushlogContextualFloatingButtonVariant {
    SOLID,
    LAYER,
    ;

    @Composable
    internal fun surfaceColor(pressed: Boolean): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            SOLID -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
            LAYER -> if (pressed) bg.layerFloatingPressed else bg.layerFloating
        }
    }

    @Composable
    internal fun contentColor(enabled: Boolean): Color {
        val fg = PushlogTheme.color.fg
        return when {
            !enabled -> fg.disabled
            this == SOLID -> fg.onNeutralSolid
            else -> fg.neutral
        }
    }

    @Composable
    internal fun trackColor(): Color =
        when (this) {
            SOLID -> pushlogThemedColor(LightPalette.gray700, DarkPalette.gray700)
            LAYER -> pushlogThemedColor(LightPalette.gray500, DarkPalette.gray500)
        }
}
