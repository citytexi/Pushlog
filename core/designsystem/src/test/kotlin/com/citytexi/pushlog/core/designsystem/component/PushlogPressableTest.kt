package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Dp
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
class PushlogPressableTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()

    @Test
    fun scaleUsesHeightWhenTallest() =
        assertEquals((40f - 2f) / 40f, pressedScaleFactor(widthPx = 100f, heightPx = 40f, density = 1f), 1e-4f)

    @Test
    fun scaleUsesQuarterWidthForWideElements() = assertEquals((80f - 2f) / 80f, pressedScaleFactor(320f, 40f, 1f), 1e-4f)

    @Test
    fun scaleUses24dpFloorForSmallElements() = assertEquals((48f - 4f) / 48f, pressedScaleFactor(40f, 20f, 2f), 1e-4f)

    @Test
    fun enabledShrinksWhenPressed() {
        setBox(enabled = true)
        val before = paintedWidth()
        press()
        assertTrue("pressed width ${paintedWidth()} should be below $before", paintedWidth() < before)
    }

    @Test
    fun disabledDoesNotShrink() {
        setBox(enabled = false)
        val before = paintedWidth()
        press()
        assertEquals(before, paintedWidth())
    }

    @Test
    fun loadingDoesNotShrink() {
        setBox(enabled = true, loading = true)
        val before = paintedWidth()
        press()
        assertEquals(before, paintedWidth())
    }

    @Test
    fun pressedSwapsBackgroundOnly() {
        setBox(enabled = true)
        press()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertEquals(PRESSED.toArgb(), image[image.width / 2, image.height / 2].toArgb())
    }

    private fun setBox(
        enabled: Boolean,
        loading: Boolean = false,
        size: Dp = 100.dp,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.size(size * 1.4f), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(size)
                            .pushlogPressable(
                                interactionSource = remember { source },
                                color = BASE,
                                pressedColor = PRESSED,
                                shape = RoundedCornerShape(0.dp),
                                enabled = enabled,
                                loading = loading,
                                onClick = {},
                            ),
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    /** 가운데 가로줄에서 표면 색으로 칠해진 픽셀 수. 레이아웃 크기는 그대로고 graphicsLayer만 줄어드는 것을 잡으려고 픽셀로 잰다. */
    private fun paintedWidth(): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val y = image.height / 2
        return (0 until image.width).count { image[it, y].toArgb() in SURFACE_COLORS }
    }

    /** disabled·loading은 clickable이 Press를 내보내지 않으므로 상호작용을 직접 주입해 modifier 자체의 방어를 본다. */
    private fun press() {
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private companion object {
        val BASE = Color(0xFF00FF00)
        val PRESSED = Color(0xFFFF0000)
        val SURFACE_COLORS = setOf(BASE.toArgb(), PRESSED.toArgb())
    }
}
