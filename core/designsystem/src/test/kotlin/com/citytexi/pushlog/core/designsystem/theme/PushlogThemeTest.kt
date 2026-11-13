package com.citytexi.pushlog.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPushlogColors
import com.citytexi.pushlog.core.designsystem.theme.color.LightPushlogColors
import com.citytexi.pushlog.core.designsystem.theme.color.PushlogColors
import com.citytexi.pushlog.core.designsystem.theme.typography.PretendardFontFamily
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PushlogThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var scheme: ColorScheme
    private lateinit var typography: Typography
    private lateinit var colors: PushlogColors

    private fun captureThemed(darkTheme: Boolean) {
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                scheme = MaterialTheme.colorScheme
                typography = MaterialTheme.typography
                colors = PushlogTheme.color
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun lightMapsBrandIntoMaterial() {
        captureThemed(darkTheme = false)
        assertEquals(Color(0xFF20874A), scheme.primary)
        assertEquals(Color.White, scheme.onPrimary)
        assertEquals(Color.Transparent, scheme.surfaceTint)
        assertEquals(colors.fg.brandContrast, scheme.onPrimaryContainer)
    }

    @Test
    fun darkSelectsDarkTokens() {
        captureThemed(darkTheme = true)
        assertEquals(DarkPushlogColors, colors)
        assertEquals(DarkPushlogColors.bg.brandSolid, scheme.primary)
    }

    @Test
    fun outlineUsesNeutralWeak() {
        captureThemed(darkTheme = false)
        assertEquals(colors.stroke.neutralWeak, scheme.outline)
    }

    @Test
    fun materialTypographyIsPretendard() {
        captureThemed(darkTheme = false)
        val t = typography
        val all =
            listOf(
                t.displayLarge,
                t.displayMedium,
                t.displaySmall,
                t.headlineLarge,
                t.headlineMedium,
                t.headlineSmall,
                t.titleLarge,
                t.titleMedium,
                t.titleSmall,
                t.bodyLarge,
                t.bodyMedium,
                t.bodySmall,
                t.labelLarge,
                t.labelMedium,
                t.labelSmall,
            )
        all.forEach { assertEquals(PretendardFontFamily, it.fontFamily) }
    }

    @Test
    fun defaultsWithoutThemeAreLight() {
        composeRule.setContent { colors = PushlogTheme.color }
        composeRule.waitForIdle()
        assertEquals(LightPushlogColors, colors)
    }
}
