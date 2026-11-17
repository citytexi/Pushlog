package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

/**
 * 선택된 라디오 마크의 색 계열.
 * 비활성 색은 SEED recipe가 tone 구분 없이 brand 값을 쓰고 두 값이 같아 tone마다 같은 gray300이다.
 */
enum class PushlogRadiomarkTone {
    NEUTRAL,
    BRAND,
    ;

    @Composable
    internal fun rootColor(
        selected: Boolean,
        enabled: Boolean,
    ): Color =
        when {
            !enabled -> if (selected) PushlogTheme.color.bg.transparent else disabledGray()
            selected -> solid(pressed = false)
            else -> PushlogTheme.color.bg.transparent
        }

    @Composable
    internal fun rootPressedColor(selected: Boolean): Color =
        if (selected) solid(pressed = true) else PushlogTheme.color.bg.transparentPressed

    /** null이면 테두리를 그리지 않는다. */
    @Composable
    internal fun strokeColor(
        selected: Boolean,
        enabled: Boolean,
    ): Color? =
        when {
            !selected -> PushlogTheme.color.stroke.neutralWeak
            enabled -> null
            else -> disabledGray()
        }

    @Composable
    internal fun dotColor(enabled: Boolean): Color {
        if (!enabled) return disabledGray()
        val fg = PushlogTheme.color.fg
        return when (this) {
            NEUTRAL -> fg.onNeutralSolid
            BRAND -> fg.onBrandSolid
        }
    }

    @Composable
    private fun solid(pressed: Boolean): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            NEUTRAL -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
            BRAND -> if (pressed) bg.brandSolidPressed else bg.brandSolid
        }
    }

    @Composable
    private fun disabledGray(): Color = pushlogThemedColor(LightPalette.gray300, DarkPalette.gray300)
}
