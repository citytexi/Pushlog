package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPushlogColors
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPushlogColors
import org.junit.Assert.assertTrue
import org.junit.Test

/** GHOST BRAND가 선택된 채 눌릴 때 `fg.brand` 체크가 `forest200` 배경 위에서 읽히는지 본다. */
class CheckmarkContrastTest {
    @Test
    fun lightGhostBrandPressedSelectedReadsAtLeast3To1() = assertAtLeast3To1(LightPushlogColors.fg.brand, LightPalette.forest200)

    @Test
    fun darkGhostBrandPressedSelectedReadsAtLeast3To1() = assertAtLeast3To1(DarkPushlogColors.fg.brand, DarkPalette.forest200)

    private fun assertAtLeast3To1(
        icon: Color,
        background: Color,
    ) {
        val la = icon.luminance()
        val lb = background.luminance()
        val ratio = (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
        assertTrue("contrast $ratio < $NON_TEXT_MIN ($icon on $background)", ratio >= NON_TEXT_MIN)
    }

    private companion object {
        const val NON_TEXT_MIN = 3.0
    }
}
