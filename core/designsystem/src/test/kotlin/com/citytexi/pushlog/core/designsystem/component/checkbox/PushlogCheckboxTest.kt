package com.citytexi.pushlog.core.designsystem.component.checkbox

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
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogCheckboxTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private var markColor = 0

    // 눌리면 마크가 눌림 색으로 바뀐다. 쉰 색만 세면 크기와 상관없이 줄어든 것처럼 보이므로 두 색을 함께 센다.
    private var pressedMarkColor = 0

    @Test
    fun labelTapTogglesState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                var checked by remember { mutableStateOf(false) }
                PushlogCheckbox(checked = checked, onCheckedChange = { checked = it }, label = LABEL)
            }
        }
        label().performClick()
        row().assertIsOn()
        label().performClick()
        row().assertIsOff()
    }

    @Test
    fun exposesCheckboxRoleAndTriState() {
        setCheckbox(state = ToggleableState.Indeterminate)
        val config = row().fetchSemanticsNode().config
        assertEquals(Role.Checkbox, config[SemanticsProperties.Role])
        assertEquals(ToggleableState.Indeterminate, config[SemanticsProperties.ToggleableState])
    }

    @Test
    fun rowIsSingleTapTarget() {
        setCheckbox()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
    }

    @Test
    fun disabledIgnoresTap() {
        var clicks = 0
        setCheckbox(enabled = false, onClick = { clicks++ }, interactionSource = source)
        row().assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
        val before = markPixels()
        assertTrue("disabled mark should be drawn, counted $before", before > 0)
        press()
        assertEquals(before, markPixels())
    }

    @Test
    fun pressShrinksMarkOnly() {
        setCheckbox(interactionSource = source)
        val marks = markPixels()
        val label = labelImage()
        press()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $marks", after < marks)
        assertTrue("label pixels stay the same", label.sameAs(labelImage()))
    }

    @Test
    fun realPressOnLabelShrinksMark() {
        setCheckbox()
        val before = markPixels()
        label().performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $before", after < before)
    }

    @Test
    fun multiLineLabelKeepsMarkOnFirstLine() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                markColor =
                    PushlogTheme.color.bg.neutralSolid
                        .toArgb()
                Box(Modifier.width(120.dp)) {
                    PushlogCheckbox(state = ToggleableState.On, onClick = {}, label = LONG_LABEL)
                }
            }
        }
        composeRule.waitForIdle()
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val density = composeRule.density.density
        val markWidth = (20 * density).toInt()
        val firstY = (0 until image.height).first { y -> (0 until markWidth).any { image[it, y].toArgb() == markColor } }
        val expected = (32 - 20) / 2 * density
        assertTrue("mark top $firstY should be near $expected", abs(firstY - expected) <= 1f)
        assertTrue("label wraps below the mark row", image.height > 32 * density)
    }

    @Test
    fun largeFontScaleGrowsRow() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                PushlogTheme(darkTheme = false) {
                    PushlogCheckbox(state = ToggleableState.Off, onClick = {}, label = LABEL)
                }
            }
        }
        composeRule.waitForIdle()
        val height = row().fetchSemanticsNode().size.height
        assertTrue("row height $height should exceed 32dp", height > 32 * composeRule.density.density)
    }

    @Test
    fun checkboxGroupIsSelectableGroup() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogCheckboxGroup(Modifier.testTag(GROUP)) {
                    PushlogCheckbox(checked = true, onCheckedChange = {}, label = LABEL)
                }
            }
        }
        val config = composeRule.onNodeWithTag(GROUP).fetchSemanticsNode().config
        assertTrue(config.contains(SemanticsProperties.SelectableGroup))
    }

    private fun setCheckbox(
        state: ToggleableState = ToggleableState.On,
        enabled: Boolean = true,
        onClick: () -> Unit = {},
        interactionSource: MutableInteractionSource? = null,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                // 비활성 SQUARE 마크는 bg.disabled로 칠한다. 같은 색을 세야 눌림 축소 여부를 볼 수 있다.
                val bg = PushlogTheme.color.bg
                markColor = (if (enabled) bg.neutralSolid else bg.disabled).toArgb()
                pressedMarkColor = (if (enabled) bg.neutralSolidPressed else bg.disabled).toArgb()
                PushlogCheckbox(
                    state = state,
                    onClick = onClick,
                    label = LABEL,
                    enabled = enabled,
                    interactionSource = interactionSource,
                )
            }
        }
        composeRule.waitForIdle()
    }

    private fun row() = composeRule.onNode(hasClickAction())

    private fun label() = composeRule.onNodeWithText(LABEL, useUnmergedTree = true)

    /** 라벨 앞 마크 구간(x < 20dp)에서 마크 색(쉰 색, 눌림 색) 픽셀 수. 눌림 축소는 graphicsLayer라 레이아웃이 아니라 픽셀로 잰다. */
    private fun markPixels(): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        val markWidth = (20 * composeRule.density.density).toInt()
        var count = 0
        for (x in 0 until minOf(markWidth, image.width)) {
            for (y in 0 until image.height) {
                val color = image[x, y].toArgb()
                if (color == markColor || color == pressedMarkColor) count++
            }
        }
        return count
    }

    private fun labelImage(): PixelMap = label().captureToImage().toPixelMap()

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

    private companion object {
        const val LABEL = "알림"
        const val GROUP = "group"
        const val LONG_LABEL = "아주 긴 라벨이 좁은 줄에서 여러 줄로 넘어가는지 확인하는 문장입니다"
    }
}
