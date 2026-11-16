package com.citytexi.pushlog.core.designsystem.component.pressable

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogPressableTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private var lastPress: PressInteraction.Press? = null

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
    fun reduceMotionToggledWhileShownTakesEffect() {
        setBox(enabled = true)
        val idle = paintedWidth()
        setAnimatorScale(0f)
        press()
        assertEquals("no shrink while animations are removed", idle, paintedWidth())
        release()
        setAnimatorScale(1f)
        press()
        assertTrue("shrinks again once animations are back", paintedWidth() < idle)
    }

    @Test
    fun manyPressablesShareOneObserver() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column {
                    repeat(20) {
                        Box(
                            Modifier.size(20.dp).pushlogPressable(
                                interactionSource = remember { MutableInteractionSource() },
                                color = BASE,
                                pressedColor = PRESSED,
                                shape = RoundedCornerShape(0.dp),
                                enabled = true,
                                loading = false,
                                onClick = {},
                            ),
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val resolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        val uri = Settings.Global.getUriFor(Settings.Global.ANIMATOR_DURATION_SCALE)
        assertEquals(1, shadowOf(resolver).getContentObservers(uri).size)
    }

    @Test
    fun loadingIsNotDisabled() {
        var clicks = 0
        setBox(enabled = true, loading = true, onClick = { clicks++ })
        composeRule.onNode(hasClickAction()).assertIsEnabled().performClick()
        assertEquals(0, clicks)
        assertFalse(
            composeRule
                .onNode(hasClickAction())
                .fetchSemanticsNode()
                .config
                .contains(SemanticsProperties.Disabled),
        )
    }

    @Test
    fun edgeTapSurvivesShrink() {
        var clicks = 0
        setBox(enabled = true, onClick = { clicks++ })
        val node = composeRule.onNode(hasClickAction())
        // 축소 뒤에는 가장자리 0.5px 지점이 줄어든 표면 밖이다. 판정 영역까지 줄면 살짝 움직일 때 탭이 취소된다.
        node.performTouchInput { down(Offset(0.5f, centerY)) }
        composeRule.mainClock.advanceTimeBy(300)
        node.performTouchInput {
            moveBy(Offset(0.2f, 0f))
            up()
        }
        composeRule.waitForIdle()
        assertEquals(1, clicks)
    }

    @Test
    fun loadingDoesNotShrinkOnPress() {
        setBox(enabled = true, loading = true)
        val before = paintedWidth()
        press()
        assertEquals(before, paintedWidth())
    }

    @Test
    fun pressedSwapsBackgroundOnly() {
        setBox(enabled = true) {
            Box(Modifier.align(Alignment.Center).size(20.dp).background(CONTENT))
        }
        press()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val cx = image.width / 2
        val cy = image.height / 2
        // 루트는 140dp라 30dp 아래는 눌려 줄어든 표면 안이고 20dp 내용 밖이다.
        val offset = (image.height * 30 / 140)
        assertEquals("background swaps", PRESSED.toArgb(), image[cx, cy + offset].toArgb())
        assertEquals("content keeps its color", CONTENT.toArgb(), image[cx, cy].toArgb())
    }

    @Test
    fun colorChangeReappliesWithoutPress() {
        var color by mutableStateOf(BASE)
        composeRule.setContent {
            Box(
                Modifier.size(100.dp).pushlogPressable(
                    interactionSource = remember { source },
                    color = color,
                    pressedColor = PRESSED,
                    shape = RoundedCornerShape(0.dp),
                    enabled = true,
                    loading = false,
                    onClick = {},
                ),
            )
        }
        color = OTHER
        composeRule.mainClock.advanceTimeBy(300)
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        assertEquals(OTHER.toArgb(), image[image.width / 2, image.height / 2].toArgb())
    }

    private fun setBox(
        enabled: Boolean,
        loading: Boolean = false,
        size: Dp = 100.dp,
        onClick: () -> Unit = {},
        content: @Composable BoxScope.() -> Unit = {},
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
                                onClick = onClick,
                            ),
                        content = content,
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
        composeRule.runOnIdle { lastPress = PressInteraction.Press(Offset.Zero).also { source.tryEmit(it) } }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private fun release() {
        composeRule.runOnIdle { lastPress?.let { source.tryEmit(PressInteraction.Release(it)) } }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private fun setAnimatorScale(scale: Float) {
        val resolver = ApplicationProvider.getApplicationContext<Context>().contentResolver
        Settings.Global.putFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, scale)
        composeRule.waitForIdle()
    }

    private companion object {
        val BASE = Color(0xFF00FF00)
        val PRESSED = Color(0xFFFF0000)
        val OTHER = Color(0xFF0000FF)
        val CONTENT = Color(0xFFFFFF00)
        val SURFACE_COLORS = setOf(BASE.toArgb(), PRESSED.toArgb())
    }
}
