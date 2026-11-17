package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

/**
 * 켜진 스위치 마크의 색 계열. 꺼진 트랙은 tone 구분 없이 gray600이다.
 * SEED switchmark.yaml은 BRAND에 비활성 색을 따로 두지 않아 비활성이어도 활성 색에 투명도만 건다.
 */
enum class PushlogSwitchmarkTone {
    NEUTRAL,
    BRAND,
    ;

    @Composable
    internal fun trackColor(
        checked: Boolean,
        enabled: Boolean,
    ): Color {
        if (!checked) return offTrack()
        val bg = PushlogTheme.color.bg
        return when (this) {
            NEUTRAL -> if (enabled) bg.neutralSolid else offTrack()
            BRAND -> bg.brandSolid
        }
    }

    @Composable
    internal fun thumbColor(
        checked: Boolean,
        enabled: Boolean,
    ): Color {
        val fg = PushlogTheme.color.fg
        return when (this) {
            NEUTRAL ->
                if (enabled) {
                    fg.onNeutralSolid
                } else {
                    pushlogThemedColor(LightPalette.staticBlackAlpha700, DarkPalette.staticBlackAlpha700)
                }
            BRAND -> if (checked) fg.onBrandSolid else pushlogThemedColor(LightPalette.staticWhite, DarkPalette.staticWhite)
        }
    }

    @Composable
    private fun offTrack(): Color = pushlogThemedColor(LightPalette.gray600, DarkPalette.gray600)
}
