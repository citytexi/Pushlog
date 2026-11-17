package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

/** 선택된 체크 마크의 색 계열. */
enum class PushlogCheckmarkTone {
    NEUTRAL,
    BRAND,
    ;

    @Composable
    internal fun solid(pressed: Boolean): Color {
        val bg = PushlogTheme.color.bg
        return when (this) {
            NEUTRAL -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
            BRAND -> if (pressed) bg.brandSolidPressed else bg.brandSolid
        }
    }

    @Composable
    internal fun onSolid(): Color {
        val fg = PushlogTheme.color.fg
        return when (this) {
            NEUTRAL -> fg.onNeutralSolid
            BRAND -> fg.onBrandSolid
        }
    }

    @Composable
    internal fun ghostSelectedIcon(): Color {
        val fg = PushlogTheme.color.fg
        return when (this) {
            NEUTRAL -> fg.neutral
            BRAND -> fg.brand
        }
    }

    /**
     * GHOST가 선택된 채 눌릴 때의 배경. SEED는 팔레트 200 단계를 쓴다.
     * BRAND는 다크에서도 `fg.brand` 체크와 3:1 이상 대비가 나와 라이트·다크 모두 `forest200`이다(`CheckmarkContrastTest`).
     */
    @Composable
    internal fun ghostSelectedPressed(): Color =
        when (this) {
            NEUTRAL -> pushlogThemedColor(LightPalette.gray200, DarkPalette.gray200)
            BRAND -> pushlogThemedColor(LightPalette.forest200, DarkPalette.forest200)
        }
}
