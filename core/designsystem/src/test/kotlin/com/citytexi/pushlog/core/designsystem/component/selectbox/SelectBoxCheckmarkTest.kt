package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class SelectBoxCheckmarkTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private var background = 0
    private var placeholder = 0
    private var neutralSubtle = 0
    private var neutral = 0
    private var disabled = 0

    @Test
    fun idleIconIsPlaceholder() {
        setCheckmark(selected = false, enabled = true)
        assertTrue("placeholder icon should be drawn", count(placeholder) > 0)
    }

    @Test
    fun pressedIconIsNeutralSubtle() {
        setCheckmark(selected = false, enabled = true)
        press()
        assertTrue("pressed icon should be neutralSubtle", count(neutralSubtle) > 0)
        assertEquals(0, count(placeholder))
    }

    @Test
    fun selectedIconIsNeutralEvenWhenPressed() {
        setCheckmark(selected = true, enabled = true)
        assertTrue("selected icon should be neutral", count(neutral) > 0)
        press()
        assertTrue("selected icon stays neutral while pressed", count(neutral) > 0)
        assertEquals(0, count(neutralSubtle))
    }

    @Test
    fun disabledIconIgnoresPress() {
        setCheckmark(selected = true, enabled = false)
        press()
        assertTrue("disabled icon should be disabled color", count(disabled) > 0)
        assertEquals(0, count(neutral))
    }

    @Test
    fun pressDoesNotResize() {
        setCheckmark(selected = false, enabled = true)
        val before = bounds()
        press()
        assertEquals(before, bounds())
    }

    private fun setCheckmark(
        selected: Boolean,
        enabled: Boolean,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                background =
                    PushlogTheme.color.bg.layerDefault
                        .toArgb()
                placeholder =
                    PushlogTheme.color.fg.placeholder
                        .toArgb()
                neutralSubtle =
                    PushlogTheme.color.fg.neutralSubtle
                        .toArgb()
                neutral =
                    PushlogTheme.color.fg.neutral
                        .toArgb()
                disabled =
                    PushlogTheme.color.fg.disabled
                        .toArgb()
                Box(
                    Modifier.size(40.dp).background(PushlogTheme.color.bg.layerDefault),
                    contentAlignment = Alignment.Center,
                ) {
                    SelectBoxCheckmark(selected = selected, enabled = enabled, interactionSource = source)
                }
            }
        }
        composeRule.waitForIdle()
    }

    /** 활성·비활성 모두 탭 영역이 없으므로 Press를 직접 주입하고 색 전환이 끝나게 흘린다. */
    private fun press() {
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private fun image(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    /** 캡처 이미지에서 색이 정확히 [color]인 픽셀 수. */
    private fun count(color: Int): Int {
        val image = image()
        var count = 0
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (image[x, y].toArgb() == color) count++
            }
        }
        return count
    }

    /** 배경색이 아닌 픽셀의 경계 상자(left, top, right, bottom). */
    private fun bounds(): List<Int> {
        val image = image()
        var left = Int.MAX_VALUE
        var top = Int.MAX_VALUE
        var right = -1
        var bottom = -1
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (image[x, y].toArgb() != background) {
                    left = minOf(left, x)
                    top = minOf(top, y)
                    right = maxOf(right, x)
                    bottom = maxOf(bottom, y)
                }
            }
        }
        return listOf(left, top, right, bottom)
    }
}
