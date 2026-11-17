package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.pressedScaleFactor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs
import kotlin.math.roundToInt

// 고밀도(d = 3)에서 내용 한 번 축소와 마크 이중 축소의 지름 차이가 허용 오차보다 커진다.
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w600dp-h600dp-xxhdpi")
class PushlogSelectBoxTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val source = MutableInteractionSource()
    private val selectedState = mutableStateOf(false)
    private var clicks = 0
    private var neutralSolid = 0
    private var neutralSolidPressed = 0
    private var neutralContrast = 0
    private var weakOverLayer = 0

    @Test
    fun singleExposesRadioButtonRole() {
        selectedState.value = true
        setBox(type = PushlogSelectBoxType.SINGLE)
        assertSingleRole(box())
    }

    @Test
    fun multipleExposesCheckboxRole() {
        selectedState.value = true
        setBox(type = PushlogSelectBoxType.MULTIPLE)
        assertMultipleRole(box())
    }

    @Test
    fun hiddenControlKeepsRole() {
        composeRule.setContent {
            Themed {
                Column {
                    PushlogSelectBox(
                        selected = true,
                        onClick = {},
                        type = PushlogSelectBoxType.SINGLE,
                        label = "단일",
                        showControl = false,
                        modifier = Modifier.testTag("single"),
                    )
                    PushlogSelectBox(
                        selected = true,
                        onClick = {},
                        type = PushlogSelectBoxType.MULTIPLE,
                        label = "다중",
                        showControl = false,
                        modifier = Modifier.testTag("multiple"),
                    )
                }
            }
        }
        assertSingleRole(composeRule.onNodeWithTag("single"))
        assertMultipleRole(composeRule.onNodeWithTag("multiple"))
    }

    @Test
    fun labelTapCallsOnClickOnce() {
        setBox()
        composeRule.onNodeWithText(LABEL).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun singleReTapStillCallsOnClick() {
        selectedState.value = true
        setBox(type = PushlogSelectBoxType.SINGLE)
        box().performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun cardIsSingleTapTarget() {
        setBox(description = DESCRIPTION, suffix = { Text("+1,000원") })
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
    }

    @Test
    fun disabledIgnoresPressAndTap() {
        setBox(enabled = false, interactionSource = source)
        box().assertIsNotEnabled()
        box().performClick()
        assertEquals(0, clicks)
        val before = pixels(capture())
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        assertEquals("disabled card must not change on press", before, pixels(capture()))
    }

    @Test
    fun pressShrinksContentByCardSize() {
        selectedState.value = true
        setBox(type = PushlogSelectBoxType.SINGLE, interactionSource = source, modifier = Modifier.size(300.dp, 80.dp))
        val boundsBefore = box().getBoundsInRoot()
        composeRule.runOnIdle { source.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        val d = composeRule.density.density
        val factor = pressedScaleFactor(300 * d, 80 * d, d)
        val expected = (20 * d * factor).roundToInt()
        val doubled = (20 * d * factor * 22 / 24).roundToInt()
        val span = markSpan()
        assertTrue("mark span $span should be near $expected", abs(span - expected) <= 2)
        assertTrue("mark must not shrink again by itself: $span vs $doubled", abs(span - doubled) > 2)
        assertEquals(boundsBefore, box().getBoundsInRoot())
    }

    @Test
    fun realPressOnLabelPressesMark() {
        selectedState.value = true
        setBox(type = PushlogSelectBoxType.SINGLE, modifier = Modifier.size(300.dp, 80.dp))
        composeRule.onNodeWithText(LABEL, useUnmergedTree = true).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        assertTrue("label press should press the mark", markRow().count { it == neutralSolidPressed } > 0)
    }

    @Test
    fun selectedStrokeFadesIn() {
        composeRule.mainClock.autoAdvance = false
        setBox()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle { selectedState.value = true }
        composeRule.mainClock.advanceTimeByFrame()
        val start = strokePixel(1.dp)
        assertNotEquals(neutralContrast, start)
        // 100ms 페이드의 중간이면 시작 색도 끝 색도 아니다. 바로 바뀌면 여기서 이미 끝 색이다.
        composeRule.mainClock.advanceTimeBy(48)
        val middle = strokePixel(1.dp)
        assertNotEquals(neutralContrast, middle)
        assertNotEquals(start, middle)
        composeRule.mainClock.advanceTimeBy(150)
        assertEquals(neutralContrast, strokePixel(1.dp))
    }

    @Test
    fun disabledSelectedShowsWeakTwoDpStroke() {
        selectedState.value = true
        setBox(enabled = false)
        assertEquals(weakOverLayer, strokePixel(1.5.dp))
        assertEquals(0, countPixels(capture(), neutralContrast))
    }

    @Test
    fun descriptionStopsAtTwoLines() {
        setBox(description = LONG_DESCRIPTION, modifier = Modifier.width(240.dp))
        val results = mutableListOf<TextLayoutResult>()
        composeRule
            .onNodeWithText(LONG_DESCRIPTION, useUnmergedTree = true)
            .fetchSemanticsNode()
            .config[SemanticsActions.GetTextLayoutResult]
            .action
            ?.invoke(results)
        assertEquals(2, results.first().lineCount)
    }

    @Test
    fun horizontalLayoutPadsStartBy20() {
        setBox(layout = PushlogSelectBoxLayout.HORIZONTAL)
        assertLabelInset(20.dp)
    }

    @Test
    fun verticalLayoutPadsStartBy16() {
        setBox(layout = PushlogSelectBoxLayout.VERTICAL)
        assertLabelInset(16.dp)
    }

    @Test
    fun layoutDefaultsToHorizontalOutsideGroup() {
        setBox(layout = null)
        assertLabelInset(20.dp)
    }

    private fun setBox(
        type: PushlogSelectBoxType = PushlogSelectBoxType.MULTIPLE,
        description: String? = null,
        layout: PushlogSelectBoxLayout? = null,
        enabled: Boolean = true,
        interactionSource: MutableInteractionSource? = null,
        modifier: Modifier = Modifier.width(300.dp),
        suffix: (@Composable () -> Unit)? = null,
    ) {
        composeRule.setContent {
            Themed {
                PushlogSelectBox(
                    selected = selectedState.value,
                    onClick = { clicks++ },
                    type = type,
                    label = LABEL,
                    modifier = modifier.testTag(TAG),
                    description = description,
                    layout = layout,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    suffix = suffix,
                )
            }
        }
        composeRule.waitForIdle()
    }

    /** 바탕을 고정하고 비교할 색을 토큰에서 읽는다. 알파 토큰은 바탕 위에 합성한 값으로 둔다. */
    @Composable
    private fun Themed(content: @Composable () -> Unit) {
        PushlogTheme(darkTheme = false) {
            val color = PushlogTheme.color
            neutralSolid = color.bg.neutralSolid.toArgb()
            neutralSolidPressed = color.bg.neutralSolidPressed.toArgb()
            neutralContrast = color.stroke.neutralContrast.toArgb()
            weakOverLayer =
                color.stroke.neutralWeak
                    .compositeOver(color.bg.layerDefault)
                    .toArgb()
            Box(Modifier.background(color.bg.layerDefault)) { content() }
        }
    }

    private fun box(): SemanticsNodeInteraction = composeRule.onNodeWithTag(TAG)

    private fun assertSingleRole(node: SemanticsNodeInteraction) {
        node.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)).assertIsSelected()
        assertFalse(node.fetchSemanticsNode().config.contains(SemanticsProperties.ToggleableState))
    }

    private fun assertMultipleRole(node: SemanticsNodeInteraction) {
        node.assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox)).assertIsOn()
    }

    private fun assertLabelInset(expected: Dp) {
        val d = composeRule.density.density
        val label = composeRule.onNodeWithText(LABEL, useUnmergedTree = true).getBoundsInRoot()
        val card = box().getBoundsInRoot()
        val insetPx = (label.left - card.left).value * d
        assertTrue("label inset $insetPx px should be ${expected.value * d}", abs(insetPx - expected.value * d) <= 1f)
    }

    private fun capture(): ImageBitmap = box().captureToImage()

    /** 크기와 모든 픽셀의 ARGB. 두 캡처를 그대로 비교할 수 있다. */
    private fun pixels(image: ImageBitmap): List<Int> {
        val map = image.toPixelMap()
        return listOf(map.width, map.height) + (0 until map.height).flatMap { y -> (0 until map.width).map { x -> map[x, y].toArgb() } }
    }

    private fun countPixels(
        image: ImageBitmap,
        color: Int,
    ): Int {
        val map: PixelMap = image.toPixelMap()
        var count = 0
        for (x in 0 until map.width) {
            for (y in 0 until map.height) {
                if (map[x, y].toArgb() == color) count++
            }
        }
        return count
    }

    /** 카드 세로 가운데 줄의 오른쪽 60dp 구간. 마크가 이 안에 있다. */
    private fun markRow(): List<Int> {
        val map = capture().toPixelMap()
        val y = map.height / 2
        val start = map.width - (60 * composeRule.density.density).roundToInt()
        return (start until map.width).map { map[it, y].toArgb() }
    }

    /** 마크 색 픽셀의 가장 왼쪽부터 가장 오른쪽까지의 길이. 가운데 점을 건너뛰고 지름을 잰다. */
    private fun markSpan(): Int {
        val row = markRow()
        val marks = row.indices.filter { row[it] == neutralSolid || row[it] == neutralSolidPressed }
        return if (marks.isEmpty()) 0 else marks.last() - marks.first() + 1
    }

    private fun strokePixel(x: Dp): Int {
        val map = capture().toPixelMap()
        return map[(x.value * composeRule.density.density).roundToInt(), map.height / 2].toArgb()
    }

    private companion object {
        const val TAG = "box"
        const val LABEL = "사과"
        const val DESCRIPTION = "빨갛고 단단한 과일"
        const val LONG_DESCRIPTION =
            "아주 긴 설명이 들어갑니다. 카드 너비를 넘어 여러 줄로 이어지는 설명이라 두 줄에서 말줄임표로 끝나야 합니다. " +
                "세 번째 줄과 네 번째 줄까지 이어질 만큼 충분히 길게 씁니다."
    }
}
