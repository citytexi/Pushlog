package com.citytexi.pushlog.core.designsystem.theme.color

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class BrandContrastTest {
    @Test
    fun light() = assertBrandContrast(LightPushlogColors)

    @Test
    fun dark() = assertBrandContrast(DarkPushlogColors)

    @Test
    fun lightBrandSolidIsExactBrand() = assertEquals(Color(0xFF20874A), LightPushlogColors.bg.brandSolid)

    private fun assertBrandContrast(c: PushlogColors) {
        assertAtLeast(TEXT_MIN, c.fg.onBrandSolid, c.bg.brandSolid)
        assertAtLeast(TEXT_MIN, c.fg.onBrandSolid, c.bg.brandSolidPressed)
        assertAtLeast(TEXT_MIN, c.fg.brand, c.bg.layerDefault)
        assertAtLeast(TEXT_MIN, c.fg.brandContrast, c.bg.brandWeak)
        assertAtLeast(TEXT_MIN, c.fg.neutral, c.bg.layerDefault)
        assertAtLeast(NON_TEXT_MIN, c.stroke.brandSolid, c.bg.layerDefault)
    }

    private fun assertAtLeast(
        min: Double,
        a: Color,
        b: Color,
    ) {
        val ratio = contrast(a, b)
        assertTrue("contrast $ratio < $min ($a on $b)", ratio >= min)
    }

    private fun contrast(
        a: Color,
        b: Color,
    ): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (maxOf(la, lb) + 0.05) / (minOf(la, lb) + 0.05)
    }

    private fun luminance(c: Color): Double = 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)

    private fun channel(v: Float): Double {
        val c = v.toDouble()
        return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
    }

    private companion object {
        const val TEXT_MIN = 4.5
        const val NON_TEXT_MIN = 3.0
    }
}
