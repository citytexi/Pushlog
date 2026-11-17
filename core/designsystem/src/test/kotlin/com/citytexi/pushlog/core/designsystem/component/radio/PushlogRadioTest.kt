package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogRadioTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private var markColor = 0

    // 눌리면 마크가 눌림 색으로 바뀐다. 쉰 색만 세면 크기와 상관없이 줄어든 것처럼 보이므로 두 색을 함께 센다.
    private var pressedMarkColor = 0

    @Test
    fun labelTapSelects() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                var selected by remember { mutableStateOf(FIRST) }
                PushlogRadioGroup {
                    listOf(FIRST, SECOND, THIRD).forEach { label ->
                        PushlogRadio(selected = selected == label, onClick = { selected = label }, label = label)
                    }
                }
            }
        }
        composeRule.onNodeWithText(SECOND, useUnmergedTree = true).performClick()
        radioRow(SECOND).assertIsSelected()
        radioRow(FIRST).assertIsNotSelected()
    }

    @Test
    fun exposesRadioButtonRole() {
        setRadio()
        val config = row().fetchSemanticsNode().config
        assertEquals(Role.RadioButton, config[SemanticsProperties.Role])
    }

    @Test
    fun rowIsSingleTapTarget() {
        setRadio()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
    }

    @Test
    fun disabledIgnoresTap() {
        var clicks = 0
        setRadio(enabled = false, onClick = { clicks++ }, interactionSource = source)
        row().assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
        val before = markPixels()
        assertTrue("disabled mark should be drawn, counted $before", before > 0)
        press()
        assertEquals(before, markPixels())
    }

    @Test
    fun pressShrinksMarkOnly() {
        setRadio(interactionSource = source)
        val marks = markPixels()
        assertTrue("mark should be drawn, counted $marks", marks > 0)
        val label = labelImage()
        press()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $marks", after < marks)
        assertTrue("label pixels stay the same", label.sameAs(labelImage()))
    }

    @Test
    fun realPressOnLabelShrinksMark() {
        setRadio()
        val before = markPixels()
        assertTrue("mark should be drawn, counted $before", before > 0)
        label().performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val after = markPixels()
        assertTrue("pressed mark $after should be below $before", after < before)
    }

    @Test
    fun groupIsSelectableGroup() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogRadioGroup(Modifier.testTag(GROUP)) {
                    PushlogRadio(selected = true, onClick = {}, label = FIRST)
                }
            }
        }
        val config = composeRule.onNodeWithTag(GROUP).fetchSemanticsNode().config
        assertTrue(config.contains(SemanticsProperties.SelectableGroup))
    }

    private fun setRadio(
        enabled: Boolean = true,
        onClick: () -> Unit = {},
        interactionSource: MutableInteractionSource? = null,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                // 비활성 선택 마크는 테두리와 점을 gray300으로 그린다. 같은 색을 세야 눌림 축소 여부를 볼 수 있다.
                markColor =
                    if (enabled) {
                        PushlogTheme.color.bg.neutralSolid
                            .toArgb()
                    } else {
                        pushlogThemedColor(LightPalette.gray300, DarkPalette.gray300).toArgb()
                    }
                pressedMarkColor =
                    if (enabled) {
                        PushlogTheme.color.bg.neutralSolidPressed
                            .toArgb()
                    } else {
                        markColor
                    }
                PushlogRadio(
                    selected = true,
                    onClick = onClick,
                    label = FIRST,
                    enabled = enabled,
                    interactionSource = interactionSource,
                )
            }
        }
        composeRule.waitForIdle()
    }

    private fun row() = composeRule.onNode(hasClickAction())

    private fun radioRow(label: String) = composeRule.onNode(hasClickAction() and hasText(label))

    private fun label() = composeRule.onNodeWithText(FIRST, useUnmergedTree = true)

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
        const val FIRST = "첫째"
        const val SECOND = "둘째"
        const val THIRD = "셋째"
        const val GROUP = "group"
    }
}
