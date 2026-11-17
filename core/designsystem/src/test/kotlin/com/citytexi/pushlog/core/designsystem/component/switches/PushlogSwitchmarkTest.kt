package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composer
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.CompositionTracer
import androidx.compose.runtime.InternalComposeTracingApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.LayoutDirection
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.component.pressable.pressedScaleFactor
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
import kotlin.math.roundToInt

/** 라이트, NEUTRAL, LARGE(52×32, 패딩 3, thumb 26) 스위치 마크를 흰 바탕에 그려 픽셀로 잰다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogSwitchmarkTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private var checked by mutableStateOf(false)
    private var gray600 = Color.Unspecified
    private var neutralSolid = Color.Unspecified
    private var thumb = Color.Unspecified

    private val density get() = composeRule.density.density

    @Test
    fun thumbRestsAtStartWhenOff() {
        setMark()
        assertNear("thumb left edge when off", (3f + 26f * 0.1f) * density, thumbLeft())
    }

    @Test
    fun thumbTravelsWhenChecked() {
        setMark(autoAdvance = false)
        toggleTo(true)
        composeRule.mainClock.advanceTimeBy(300)
        assertNear("thumb left edge when on", (3f + 20f) * density, thumbLeft())
    }

    /** RTL에서는 꺼짐이 오른쪽, 켜짐이 왼쪽이고 thumb은 트랙 안에 머문다. */
    @Test
    fun thumbMirrorsInRtl() {
        setMark(autoAdvance = false, layoutDirection = LayoutDirection.Rtl)
        composeRule.mainClock.advanceTimeBy(16)
        assertNear("RTL thumb left edge when off", (52f - 3f - 26f * 0.9f) * density, thumbLeft())
        toggleTo(true)
        composeRule.mainClock.advanceTimeBy(300)
        assertNear("RTL thumb left edge when on", 3f * density, thumbLeft())
    }

    @Test
    fun reversingMidwayDoesNotJump() {
        setMark(autoAdvance = false)
        toggleTo(true)
        composeRule.mainClock.advanceTimeBy(75)
        toggleTo(false)
        composeRule.mainClock.advanceTimeBy(16)
        val left = thumbLeft()
        assertTrue(
            "thumb should turn back from midway, left edge $left",
            left > 5.6f * density + 1 && left < 23f * density - 1,
        )
    }

    @Test
    fun trackColorStartsAfter20msDelay() {
        setMark(autoAdvance = false)
        toggleTo(true)
        // 첫 프레임은 다시 그리기, 둘째 프레임은 애니메이션 시작(0ms)이라 셋째 프레임(16ms)을 본다. 지연이 없으면 여기서 이미 바뀐다.
        composeRule.mainClock.advanceTimeBy(48)
        assertEquals("track should wait 20ms before changing", gray600.toArgb(), trackPixel())
        composeRule.mainClock.advanceTimeBy(150)
        assertEquals(neutralSolid.toArgb(), trackPixel())
    }

    /**
     * 색 tween(20ms 지연 + 50ms) 동안 마크는 다시 구성하지 않고 그리기만 한다.
     * 색을 구성 단계에서 읽으면 매 프레임 다시 구성된다.
     */
    @OptIn(InternalComposeTracingApi::class)
    @Test
    fun colorAnimationDoesNotRecompose() {
        setMark(autoAdvance = false)
        composeRule.mainClock.advanceTimeBy(16)
        var recompositions = 0
        val tracer =
            object : CompositionTracer {
                override fun isTraceInProgress(): Boolean = true

                override fun traceEventStart(
                    key: Int,
                    dirty1: Int,
                    dirty2: Int,
                    info: String,
                ) {
                    if (info.contains(".PushlogSwitchmark (")) recompositions++
                }

                override fun traceEventEnd() = Unit
            }
        Composer.setTracer(tracer)
        try {
            toggleTo(true)
            // checked 변경으로 다시 구성되고 애니메이션이 시작하는 프레임까지 넘긴다.
            composeRule.mainClock.advanceTimeBy(32)
            // 대조군: 추적기가 마크의 재구성을 실제로 센다. 0이면 아래 단언이 의미 없이 통과한다.
            assertTrue("tracer should see the checked recomposition, counted $recompositions", recompositions > 0)
            recompositions = 0
            composeRule.mainClock.advanceTimeBy(200)
        } finally {
            Composer.setTracer(null)
        }
        assertEquals(neutralSolid.toArgb(), trackPixel())
        assertEquals("mark should not recompose while colors animate", 0, recompositions)
    }

    @Test
    fun pressShrinksWithoutColorChange() {
        setMark(autoAdvance = false)
        composeRule.mainClock.advanceTimeBy(16)
        val restColor = trackPixel()
        assertEquals(gray600.toArgb(), restColor)
        assertNear("rest span", 52f * density, drawnSpan().toFloat())

        press()

        val factor = pressedScaleFactor(52f * density, 32f * density, density)
        assertNear("pressed span", (52f * density * factor).roundToInt().toFloat(), drawnSpan().toFloat())
        val image = capture()
        val y = image.height / 2
        val left = image.width / 2 - (22f * density).roundToInt()
        assertEquals("pressed track keeps its color", restColor, image[left, y].toArgb())
    }

    @Test
    fun noScaleIgnoresPress() {
        setMark(autoAdvance = false, scaleOnPress = false)
        press()
        assertNear("span without scale", 52f * density, drawnSpan().toFloat())
    }

    @Test
    fun disabledIsDimmedAndIgnoresPress() {
        setMark(autoAdvance = false, enabled = false)
        press()
        val expected = gray600.copy(alpha = 0.38f).compositeOver(Color.White)
        assertColorNear(expected, Color(trackPixel()))
        assertNear("disabled span", 52f * density, drawnSpan().toFloat())
    }

    private fun setMark(
        autoAdvance: Boolean = true,
        enabled: Boolean = true,
        scaleOnPress: Boolean = true,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    ) {
        composeRule.mainClock.autoAdvance = autoAdvance
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                gray600 = pushlogThemedColor(LightPalette.gray600, DarkPalette.gray600)
                neutralSolid = PushlogTheme.color.bg.neutralSolid
                thumb = PushlogTheme.color.fg.onNeutralSolid
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Box(Modifier.background(Color.White)) {
                        PushlogSwitchmark(
                            checked = checked,
                            enabled = enabled,
                            scaleOnPress = scaleOnPress,
                            interactionSource = source,
                        )
                    }
                }
            }
        }
        if (autoAdvance) composeRule.waitForIdle() else composeRule.mainClock.advanceTimeByFrame()
    }

    private fun toggleTo(value: Boolean) {
        composeRule.runOnIdle { checked = value }
        // 시계를 멈춰 두면 상태 쓰기 알림이 메인 루퍼에 머문다. 비워야 다음 프레임에 다시 그린다.
        composeRule.waitForIdle()
    }

    private fun press() {
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
    }

    private fun capture(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    /** 마크 왼쪽 끝부터 가운데 줄을 훑어 thumb 색이 처음 나오는 x. */
    private fun thumbLeft(): Float {
        val image = capture()
        val y = image.height / 2
        val x = (0 until image.width).first { image[it, y].toArgb() == thumb.toArgb() }
        return x.toFloat()
    }

    /** 가운데 줄, x = 2dp의 트랙 색. thumb보다 왼쪽이다. */
    private fun trackPixel(): Int {
        val image = capture()
        return image[(2f * density).roundToInt(), image.height / 2].toArgb()
    }

    /**
     * 가운데 줄에서 바탕(흰색)이 아닌 첫 픽셀부터 마지막 픽셀까지의 길이. 트랙 ∪ thumb 폭이다.
     * 라이트 NEUTRAL thumb은 gray00(흰색)이라 색 개수로는 바탕과 섞이므로 트랙 양끝 사이를 잰다.
     */
    private fun drawnSpan(): Int {
        val image = capture()
        val y = image.height / 2
        val white = Color.White.toArgb()
        val track = (0 until image.width).count { image[it, y].toArgb() != white && image[it, y].toArgb() != thumb.toArgb() }
        assertTrue("track should be drawn on the middle row, counted $track", track > 0)
        val first = (0 until image.width).first { image[it, y].toArgb() != white }
        val last = (0 until image.width).last { image[it, y].toArgb() != white }
        return last - first + 1
    }

    private fun assertNear(
        message: String,
        expected: Float,
        actual: Float,
    ) {
        assertTrue("$message: $actual should be within 1px of $expected", abs(actual - expected) <= 1f)
    }

    private fun assertColorNear(
        expected: Color,
        actual: Color,
    ) {
        val channels = listOf(expected.red to actual.red, expected.green to actual.green, expected.blue to actual.blue)
        channels.forEach { (e, a) ->
            assertTrue("expected $expected, got $actual", abs(e * 255 - a * 255) <= 2f)
        }
    }
}
