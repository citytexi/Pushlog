package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

enum class PushlogActionButtonVariant {
    BRAND_SOLID,
    NEUTRAL_SOLID,
    NEUTRAL_WEAK,
    CRITICAL_SOLID,
    GHOST,
    ;

    @Composable
    internal fun surfaceColor(pressed: Boolean): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            BRAND_SOLID -> if (pressed) bg.brandSolidPressed else bg.brandSolid
            NEUTRAL_SOLID -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
            NEUTRAL_WEAK -> if (pressed) bg.neutralWeakPressed else bg.neutralWeak
            CRITICAL_SOLID -> if (pressed) bg.criticalSolidPressed else bg.criticalSolid
            GHOST -> if (pressed) bg.transparentPressed else bg.transparent
        }
    }

    @Composable
    internal fun disabledSurfaceColor(): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            BRAND_SOLID, NEUTRAL_SOLID, NEUTRAL_WEAK, CRITICAL_SOLID -> bg.disabled
            GHOST -> bg.transparent
        }
    }

    @Composable
    internal fun contentColor(enabled: Boolean): Color {
        val fg = PushlogTheme.color.fg
        if (!enabled) return fg.disabled
        return when (this) {
            BRAND_SOLID -> fg.onBrandSolid
            NEUTRAL_SOLID -> fg.onNeutralSolid
            CRITICAL_SOLID -> fg.onCriticalSolid
            NEUTRAL_WEAK, GHOST -> fg.neutral
        }
    }

    @Composable
    internal fun trackColor(): Color =
        when (this) {
            BRAND_SOLID, NEUTRAL_SOLID, CRITICAL_SOLID ->
                pushlogThemedColor(
                    LightPalette.staticWhiteAlpha300,
                    DarkPalette.staticWhiteAlpha300,
                )
            NEUTRAL_WEAK, GHOST -> pushlogThemedColor(LightPalette.gray500, DarkPalette.gray500)
        }
}
