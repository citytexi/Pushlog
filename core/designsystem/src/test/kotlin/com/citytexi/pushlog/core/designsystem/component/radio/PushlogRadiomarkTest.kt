package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogRadiomarkTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()

    /** PR3 select-box가 이 조합(MEDIUM, NEUTRAL, scaleOnPress = false)을 쓴다. */
    @Test
    fun noScaleKeepsSizeWhilePressed() {
        var solid = 0
        var pressed = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                val bg = PushlogTheme.color.bg
                solid = bg.neutralSolid.toArgb()
                pressed = bg.neutralSolidPressed.toArgb()
                PushlogRadiomark(selected = true, scaleOnPress = false, interactionSource = source)
            }
        }
        composeRule.waitForIdle()
        val before = rootPixelsOnLine(solid, pressed)
        assertTrue("selected mark should be drawn above the dot, counted $before", before > 0)

        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(400)
        composeRule.waitForIdle()

        assertEquals(before, rootPixelsOnLine(solid, pressed))
        val image = capture()
        val center = image.width / 2
        assertEquals(pressed, image[center, lineAboveDot(image)].toArgb())
    }

    @Test
    fun disabledSelectedDotIsLarger() {
        var gray300 = 0
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                gray300 = pushlogThemedColor(LightPalette.gray300, DarkPalette.gray300).toArgb()
                PushlogRadiomark(selected = true, enabled = false)
            }
        }
        composeRule.waitForIdle()
        val image = capture()
        val y = image.height / 2
        val center = image.width / 2
        assertEquals("disabled dot should be gray300", gray300, image[center, y].toArgb())
        // 점 가장자리는 안티에일리어싱으로 gray300과 바탕의 중간색이라, 점과 테두리 사이 바탕색이 아닌 구간을 잰다.
        val background = image[center - (7 * composeRule.density.density).toInt(), y].toArgb()
        assertTrue("gap between dot and stroke should not be gray300", background != gray300)
        var left = center
        while (left > 0 && image[left - 1, y].toArgb() != background) left--
        var right = center
        while (right < image.width - 1 && image[right + 1, y].toArgb() != background) right++
        val diameter = right - left + 1
        val expected = 10 * composeRule.density.density
        assertTrue("dot diameter $diameter should be near $expected", abs(diameter - expected) <= 1f)
    }

    private fun capture(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    /** 마크 가운데에서 6dp 위 가로줄. 8dp 점보다 위라 바탕색만 지난다. */
    private fun lineAboveDot(image: PixelMap): Int = image.height / 2 - (6 * composeRule.density.density).toInt()

    private fun rootPixelsOnLine(vararg colors: Int): Int {
        val image = capture()
        val y = lineAboveDot(image)
        return (0 until image.width).count { x -> image[x, y].toArgb() in colors }
    }
}
