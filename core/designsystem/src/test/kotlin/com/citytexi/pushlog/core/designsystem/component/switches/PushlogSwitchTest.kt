package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

/** 라이트, NEUTRAL, 켜짐 스위치 줄을 흰 바탕에 그려 픽셀과 의미 정보로 잰다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogSwitchTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()

    // 켜진 트랙 색. 라이트 NEUTRAL thumb은 흰색이라 바탕과 섞이므로 트랙 색만 센다. 눌러도 색은 그대로라 한 색이면 된다.
    private var trackColor = Color.Unspecified

    private val density get() = composeRule.density.density

    @Test
    fun labelTapToggles() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                var checked by remember { mutableStateOf(false) }
                PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = LABEL)
            }
        }
        label().performClick()
        row().assertIsOn()
        label().performClick()
        row().assertIsOff()
    }

    @Test
    fun exposesSwitchRole() {
        setSwitch(checked = false)
        val config = row().fetchSemanticsNode().config
        assertEquals(Role.Switch, config[SemanticsProperties.Role])
        row().assertIsOff()
    }

    @Test
    fun rowIsSingleTapTarget() {
        setSwitch()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
    }

    @Test
    fun disabledIgnoresTap() {
        var changes = 0
        setSwitch(enabled = false, onCheckedChange = { changes++ }, interactionSource = source)
        row().assertIsNotEnabled().performClick()
        assertEquals(0, changes)
        val before = markPixels()
        assertTrue("disabled mark should be drawn, counted $before", before > 0)
        press()
        assertEquals(before, markPixels())
    }

    @Test
    fun disabledDimsLabel() {
        var enabled by mutableStateOf(true)
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Box(Modifier.background(Color.White)) {
                    PushlogSwitch(checked = true, onCheckedChange = {}, label = LABEL, enabled = enabled)
                }
            }
        }
        composeRule.waitForIdle()
        val enabledDarkest = labelImage().darkest()
        composeRule.runOnIdle { enabled = false }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val disabledDarkest = labelImage().darkest()
        assertTrue("label should be drawn when enabled, darkest $enabledDarkest", enabledDarkest < 1f)
        assertTrue(
            "disabled label darkest $disabledDarkest should be lighter than $enabledDarkest",
            disabledDarkest > enabledDarkest + 0.1f,
        )
    }

    @Test
    fun pressShrinksMarkOnly() {
        setSwitch(interactionSource = source)
        val before = markPixels()
        assertTrue("mark should be drawn, counted $before", before > 0)
        val label = labelImage()
        press()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $before", after < before)
        assertTrue("label pixels stay the same", label.sameAs(labelImage()))
    }

    @Test
    fun realPressOnLabelShrinksMark() {
        setSwitch()
        val before = markPixels()
        assertTrue("mark should be drawn, counted $before", before > 0)
        label().performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $before", after < before)
    }

    @Test
    fun hiddenLabelBecomesAccessibilityName() {
        var changedTo: Boolean? = null
        setSwitch(checked = false, showLabel = false, onCheckedChange = { changedTo = it })
        composeRule.onAllNodesWithContentDescription(LABEL).assertCountEquals(1)
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
        val node = composeRule.onNodeWithContentDescription(LABEL)
        val config = node.fetchSemanticsNode().config
        assertEquals(Role.Switch, config[SemanticsProperties.Role])
        assertEquals(ToggleableState.Off, config[SemanticsProperties.ToggleableState])
        node.performClick()
        assertEquals(true, changedTo)
        composeRule.onNodeWithText(LABEL).assertDoesNotExist()
    }

    @Test
    fun markOnlyRowKeepsRowMinHeight() {
        val expected =
            mapOf(
                PushlogSwitchmarkSize.SMALL to 24f,
                PushlogSwitchmarkSize.MEDIUM to 24f,
                PushlogSwitchmarkSize.LARGE to 32f,
            )
        var size by mutableStateOf(PushlogSwitchmarkSize.SMALL)
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogSwitch(checked = true, onCheckedChange = {}, label = LABEL, showLabel = false, size = size)
            }
        }
        expected.forEach { (markSize, rowHeight) ->
            composeRule.runOnIdle { size = markSize }
            composeRule.waitForIdle()
            val height = row().fetchSemanticsNode().size.height
            assertNear("$markSize mark-only row height", rowHeight * density, height.toFloat())
        }
    }

    @Test
    fun smallSizeKeepsRow24dp() {
        setSwitch(size = PushlogSwitchmarkSize.SMALL)
        val height = row().fetchSemanticsNode().size.height
        assertNear("SMALL row height", 24f * density, height.toFloat())
        assertNear("SMALL mark top", 4f * density, firstTrackY().toFloat())
    }

    @Test
    fun multiLineLabelKeepsMarkOnFirstLine() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                trackColor = PushlogTheme.color.bg.neutralSolid
                Box(Modifier.width(140.dp).background(Color.White)) {
                    PushlogSwitch(checked = true, onCheckedChange = {}, label = LONG_LABEL)
                }
            }
        }
        composeRule.waitForIdle()
        assertNear("LARGE mark top", 0f, firstTrackY().toFloat())
        val height = row().fetchSemanticsNode().size.height
        assertTrue("label wraps below the mark row, height $height", height > 32 * density)
    }

    @Test
    fun largeFontScaleGrowsRow() {
        composeRule.setContent {
            val current = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(current.density, fontScale = 2f)) {
                PushlogTheme(darkTheme = false) {
                    trackColor = PushlogTheme.color.bg.neutralSolid
                    Box(Modifier.background(Color.White)) {
                        PushlogSwitch(
                            checked = true,
                            onCheckedChange = {},
                            label = LABEL,
                            size = PushlogSwitchmarkSize.SMALL,
                        )
                    }
                }
            }
        }
        composeRule.waitForIdle()
        val height = row().fetchSemanticsNode().size.height
        assertTrue("row height $height should exceed 24dp", height > 24 * density)
        // 마크 위쪽 여백은 rowMinHeight 기준이라 글자가 커져도 4dp에 머문다.
        assertNear("SMALL mark top at 2x font", 4f * density, firstTrackY().toFloat())
        label().assertIsDisplayed()
    }

    private fun setSwitch(
        checked: Boolean = true,
        enabled: Boolean = true,
        showLabel: Boolean = true,
        size: PushlogSwitchmarkSize = PushlogSwitchmarkSize.LARGE,
        onCheckedChange: (Boolean) -> Unit = {},
        interactionSource: MutableInteractionSource? = null,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                // 비활성 켜진 NEUTRAL 트랙은 gray600(= neutralSolid가 아님)에 0.38 투명도다. 그려진 그 색을 센다.
                val solid = PushlogTheme.color.bg.neutralSolid
                trackColor =
                    if (enabled) {
                        solid
                    } else {
                        PushlogSwitchmarkTone.NEUTRAL
                            .trackColor(
                                checked,
                                false,
                            ).copy(alpha = 0.38f)
                            .compositeOver(Color.White)
                    }
                Box(Modifier.background(Color.White)) {
                    PushlogSwitch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        label = LABEL,
                        showLabel = showLabel,
                        size = size,
                        enabled = enabled,
                        interactionSource = interactionSource,
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun row() = composeRule.onNode(hasClickAction())

    private fun label() = composeRule.onNodeWithText(LABEL, useUnmergedTree = true)

    private fun capture(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun isTrack(color: Color): Boolean =
        abs(color.red - trackColor.red) * 255 <= 2 &&
            abs(color.green - trackColor.green) * 255 <= 2 &&
            abs(color.blue - trackColor.blue) * 255 <= 2

    /** 라벨 앞 마크 구간(x < 52dp)의 트랙 색 픽셀 수. 눌림 축소는 graphicsLayer라 레이아웃이 아니라 픽셀로 잰다. */
    private fun markPixels(): Int {
        val image = capture()
        val markWidth = (52 * density).toInt()
        var count = 0
        for (x in 0 until minOf(markWidth, image.width)) {
            for (y in 0 until image.height) {
                if (isTrack(image[x, y])) count++
            }
        }
        return count
    }

    /** 트랙 색이 처음 나오는 y. */
    private fun firstTrackY(): Int {
        val image = capture()
        val markWidth = minOf((52 * density).toInt(), image.width)
        return (0 until image.height).first { y -> (0 until markWidth).any { isTrack(image[it, y]) } }
    }

    private fun labelImage(): PixelMap = label().captureToImage().toPixelMap()

    /** 가장 어두운 픽셀의 밝기(0 검정 ~ 1 흰색). */
    private fun PixelMap.darkest(): Float {
        var min = 1f
        for (x in 0 until width) {
            for (y in 0 until height) {
                val c = this[x, y]
                min = minOf(min, (c.red + c.green + c.blue) / 3f)
            }
        }
        return min
    }

    private fun PixelMap.sameAs(other: PixelMap): Boolean =
        width == other.width &&
            height == other.height &&
            (0 until width).all { x -> (0 until height).all { y -> this[x, y] == other[x, y] } }

    /** 비활성 줄은 탭 영역이 Press를 내보내지 않으므로 상호작용을 직접 주입한다. */
    private fun press() {
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
    }

    private fun assertNear(
        message: String,
        expected: Float,
        actual: Float,
    ) {
        assertTrue("$message: $actual should be within 1px of $expected", abs(actual - expected) <= 1f)
    }

    private companion object {
        const val LABEL = "알림"
        const val LONG_LABEL = "아주 긴 라벨이 좁은 줄에서 여러 줄로 넘어가는지 확인하는 문장입니다"
    }
}
