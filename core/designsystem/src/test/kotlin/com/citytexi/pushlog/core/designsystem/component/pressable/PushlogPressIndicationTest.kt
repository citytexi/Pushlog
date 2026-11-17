package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
class PushlogPressIndicationTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()

    @Test
    fun indicationShrinksAndSwapsColor() {
        setIndication(enabled = true, scale = true)
        val before = paintedWidth(BASE)
        press()
        // 20dp 마크의 기준 길이는 24dp라 22/24로 줄어든다.
        assertTrue("pressed width ${paintedWidth(PRESSED)} should be below $before", paintedWidth(PRESSED) < before)
        assertEquals(PRESSED.toArgb(), centerPixel())
    }

    @Test
    fun indicationWithoutScaleChangesColorOnly() {
        setIndication(enabled = true, scale = false)
        val before = paintedWidth(BASE)
        press()
        assertEquals(before, paintedWidth(PRESSED))
        assertEquals(PRESSED.toArgb(), centerPixel())
    }

    @Test
    fun disabledIndicationIgnoresPress() {
        setIndication(enabled = false, scale = true)
        val before = paintedWidth(BASE)
        press()
        assertEquals(before, paintedWidth(BASE))
        assertEquals(BASE.toArgb(), centerPixel())
    }

    @Test
    fun pressScaleShrinksWithoutDrawing() {
        setContent { Box(Modifier.size(20.dp).pushlogPressScale(source, enabled = true).background(BASE)) }
        val before = paintedWidth(BASE)
        press()
        assertTrue("pressed width ${paintedWidth(BASE)} should be below $before", paintedWidth(BASE) < before)
    }

    private fun setIndication(
        enabled: Boolean,
        scale: Boolean,
    ) = setContent {
        Box(
            Modifier
                .size(20.dp)
                .pushlogPressIndication(source, BASE, PRESSED, RectangleShape, enabled, scale),
        )
    }

    private fun setContent(mark: @Composable () -> Unit) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) { mark() }
            }
        }
        composeRule.waitForIdle()
    }

    /** 가운데 가로줄에서 [color]로 칠해진 픽셀 수. graphicsLayer만 줄어드는 것을 잡으려고 픽셀로 잰다. */
    private fun paintedWidth(color: Color): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val y = image.height / 2
        return (0 until image.width).count { image[it, y].toArgb() == color.toArgb() }
    }

    private fun centerPixel(): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        return image[image.width / 2, image.height / 2].toArgb()
    }

    /** 탭 처리가 없는 modifier라 상호작용을 직접 주입한다. */
    private fun press() {
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private companion object {
        val BASE = Color(0xFF00FF00)
        val PRESSED = Color(0xFFFF0000)
    }
}
