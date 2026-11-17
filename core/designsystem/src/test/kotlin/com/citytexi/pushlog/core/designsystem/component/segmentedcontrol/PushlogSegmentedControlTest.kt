package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.test.assertFailsWith

/**
 * 라이트 세그먼트 컨트롤을 흰 바탕에 그려 칸 크기, 선택 의미 정보, indicator 위치를 잰다.
 * indicator는 흰색(gray00)이고 컨테이너는 반투명 회색이라, 컨트롤 세로 가운데 줄에서 흰 픽셀을 찾아 indicator 끝을 잰다.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w600dp-h600dp")
class PushlogSegmentedControlTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var selected by mutableIntStateOf(0)

    // 기대 색. 컴포지션 안에서 PushlogTheme.color로 읽어 둔다.
    private var weakPressed = Color.Unspecified
    private var strokeMuted = Color.Unspecified
    private var bgDisabled = Color.Unspecified

    private val density get() = composeRule.density.density

    @Test
    fun rejectsFewerThanTwoOptions() {
        assertFailsWith<IllegalArgumentException> {
            composeRule.setContent {
                PushlogTheme(darkTheme = false) { PushlogSegmentedControl(listOf(opt("A")), 0, {}) }
            }
        }
    }

    @Test
    fun rejectsMoreThanFourOptions() {
        assertFailsWith<IllegalArgumentException> {
            composeRule.setContent {
                PushlogTheme(darkTheme = false) {
                    PushlogSegmentedControl(listOf("A", "B", "C", "D", "E").map(::opt), 0, {})
                }
            }
        }
    }

    @Test
    fun rejectsSelectedIndexOutOfRange() {
        assertFailsWith<IllegalArgumentException> {
            composeRule.setContent {
                PushlogTheme(darkTheme = false) { PushlogSegmentedControl(THREE.map(::opt), 3, {}) }
            }
        }
    }

    @Test
    fun optionsAreRadioButtonsInSelectableGroup() {
        setControl(THREE, initial = 1)
        THREE.forEach { label ->
            assertEquals(Role.RadioButton, item(label).fetchSemanticsNode().config[SemanticsProperties.Role])
        }
        item(THREE[1]).assertIsSelected()
        item(THREE[0]).assertIsNotSelected()
        item(THREE[2]).assertIsNotSelected()
        val config = control().fetchSemanticsNode().config
        assertTrue("container is a selectable group", config.contains(SemanticsProperties.SelectableGroup))
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(3)
    }

    @Test
    fun tapSelectsOption() {
        setControl(THREE)
        item(THREE[1]).performClick()
        item(THREE[1]).assertIsSelected()
        item(THREE[0]).assertIsNotSelected()
        assertEquals(1, selected)
    }

    @Test
    fun tappingSelectedOptionDoesNotNotify() {
        var changes = 0
        setControl(THREE, onChange = { changes++ })
        item(THREE[0]).performClick()
        assertEquals(0, changes)
    }

    @Test
    fun equalWidthCellsFromWidestLabel() {
        val labels = listOf("가", "아주 긴 라벨 하나", "나")
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column {
                    PushlogSegmentedControl(
                        options = labels.map(::opt),
                        selectedIndex = 0,
                        onSelectedIndexChange = {},
                        modifier = Modifier.testTag(CONTROL),
                    )
                    // 같은 라벨 항목을 제약 없이 따로 그려 자연 너비를 잰다.
                    SegmentedControlItem(
                        label = labels[1],
                        selected = false,
                        enabled = true,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {},
                        modifier = Modifier.testTag(NATURAL),
                    )
                }
            }
        }
        composeRule.waitForIdle()
        val widths = labels.map { item(it).bounds().width }
        widths.forEach { assertNear("cell width", widths[0], it) }
        val natural = composeRule.onNodeWithTag(NATURAL).bounds().width
        val cell = maxOf(86f * density, natural)
        assertTrue("the long label is wider than the minimum, natural $natural", natural > 86f * density)
        assertNear("cell width", cell, widths[1])
        assertNear("control width", 3 * cell + 8f * density, control().bounds().width, tolerance = 2f)
    }

    @Test
    fun shortLabelsUseMinimumWidth() {
        setControl(listOf("A", "B"))
        listOf("A", "B").forEach { label ->
            val bounds = item(label).bounds()
            assertNear("item width", 86f * density, bounds.width)
            assertNear("item height", 34f * density, bounds.height)
        }
        assertNear("control height", 42f * density, control().bounds().height)
    }

    @Test
    fun narrowContainerWrapsLabels() {
        val labels = listOf("아주 긴 첫째 라벨", "아주 긴 둘째 라벨", "아주 긴 셋째 라벨")
        setControl(labels) { content -> Box(Modifier.width(200.dp)) { content() } }
        val bounds = labels.map { item(it).bounds() }
        bounds.forEach { assertNear("item width", (200f - 8f) / 3f * density, it.width) }
        bounds.forEach { assertNear("item height", bounds[0].height, it.height) }
        assertTrue("labels wrap, height ${bounds[0].height}", bounds[0].height > 34f * density)
        assertNear("control width", 200f * density, control().bounds().width)
    }

    @Test
    fun fillMaxWidthStretchesCellsAndIndicator() {
        setControl(listOf("A", "B"), initial = 1, modifier = Modifier.fillMaxWidth()) { content ->
            Box(Modifier.width(400.dp)) { content() }
        }
        listOf("A", "B").forEach { assertNear("item width", 196f * density, item(it).bounds().width) }
        assertNear("indicator start", (4f + 196f) * density, indicatorStart().toFloat())
    }

    @Test
    fun initialIndicatorStartsAtSelectedSlot() {
        setControl(THREE, initial = 2, modifier = Modifier.width(300.dp), autoAdvance = false)
        assertNear("indicator start", (4f + 292f * 2 / 3) * density, indicatorStart().toFloat())
        // 292dp는 3으로 나누어떨어지지 않아 칸 너비가 1px 다를 수 있다. indicator는 그 칸의 경계에 정확히 맞는다.
        val cell = item(THREE[2]).bounds()
        val left = control().bounds().left
        assertEquals("indicator start on the cell edge", (cell.left - left).roundToInt(), indicatorStart())
        assertEquals("indicator end on the cell edge", cell.right.roundToInt(), indicatorEnd())
    }

    @Test
    fun indicatorMovesToNewSlot() {
        setControl(THREE, modifier = Modifier.width(300.dp), autoAdvance = false)
        val end = (4f + 292f * 2 / 3) * density
        composeRule.runOnIdle { selected = 2 }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeBy(100)
        val midway = indicatorStart().toFloat()
        assertTrue("indicator $midway should be between the slots", midway > 4f * density + 2 && midway < end - 2)
        composeRule.mainClock.advanceTimeBy(300)
        assertNear("indicator start", end, indicatorStart().toFloat())
    }

    @Test
    fun rtlPlacesIndicatorUnderSelectedItem() {
        setControl(THREE, modifier = Modifier.width(300.dp), layoutDirection = LayoutDirection.Rtl)
        val control = control().bounds()
        val first = item(THREE[0]).bounds()
        assertNear("first item sits at the right end", control.right - 4f * density, first.right)
        assertNear("indicator end", control.right - 4f * density, indicatorEnd().toFloat())
    }

    @Test
    fun largeFontKeepsEqualHeights() {
        val labels = listOf("짧음", "아주 긴 라벨이 여러 줄로 넘어간다", "짧음2")
        setControl(labels, initial = 1, fontScale = 2f) { content -> Box(Modifier.width(240.dp)) { content() } }
        val bounds = labels.map { item(it).bounds() }
        bounds.forEach { assertNear("item height", bounds[0].height, it.height) }
        val (top, bottom) = indicatorVerticalRange()
        assertNear("indicator top", bounds[1].top, top.toFloat())
        assertNear("indicator bottom", bounds[1].bottom, bottom.toFloat())
    }

    @Test
    fun largeFontNarrowControlDoesNotClipIndicator() {
        val labels = listOf("New items", "Old items")
        setControl(labels, fontScale = 2f) { content -> Box(Modifier.width(200.dp)) { content() } }
        val cell = item(labels[0]).bounds()
        assertTrue("cell should be taller than wide, ${cell.width}x${cell.height}", cell.height > cell.width + 8f * density)
        // indicator 둥근 끝(반지름 = 칸 너비/2) 바로 아래 줄은 indicator 옆면이 곧고 컨테이너 모서리는 아직 휘어 있다.
        val y = (cell.top + cell.width / 2 + 2f * density).roundToInt()
        assertNear("indicator start on its straight edge", 4f * density, indicatorStart(y).toFloat())
    }

    @Test
    fun pressedUnselectedShowsColorAndBorder() {
        setControl(THREE, autoAdvance = false, keepSelection = true)
        val before = borderPixel(THREE[1])
        item(THREE[1]).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        assertColorNear("pressed border", strokeMuted.compositeOver(weakPressed), borderPixel(THREE[1]))
        assertColorNear("pressed surface", weakPressed, surfacePixel(THREE[1]))
        item(THREE[1]).performTouchInput { up() }
        composeRule.mainClock.advanceTimeBy(300)
        assertColorNear("released border", before, borderPixel(THREE[1]))
    }

    @Test
    fun pressedSelectedItemCoversIndicatorWithGray100() {
        setControl(THREE, autoAdvance = false)
        item(THREE[0]).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        assertColorNear("pressed selected surface", LightPalette.gray100, surfacePixel(THREE[0]))
        assertColorNear("pressed selected border", strokeMuted.compositeOver(LightPalette.gray100), borderPixel(THREE[0]))
        item(THREE[0]).performTouchInput { up() }
        composeRule.mainClock.advanceTimeBy(300)
        assertColorNear("released selected surface", LightPalette.gray00, surfacePixel(THREE[0]))
    }

    @Test
    fun pressShrinksLabelOnly() {
        setControl(THREE, autoAdvance = false, keepSelection = true)
        val bounds = item(THREE[1]).bounds()
        val (beforeStart, beforeEnd) = labelRange(THREE[1])
        assertTrue("label should be drawn, $beforeStart..$beforeEnd", beforeEnd > beforeStart)
        item(THREE[1]).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        val (afterStart, afterEnd) = labelRange(THREE[1])
        assertTrue(
            "label $afterStart..$afterEnd should be narrower than $beforeStart..$beforeEnd",
            afterEnd - afterStart < beforeEnd - beforeStart,
        )
        assertEquals("item bounds stay the same", bounds, item(THREE[1]).bounds())
    }

    @Test
    fun disabledOptionIgnoresTap() {
        var changes = 0
        setControl(THREE, disabled = setOf(1), onChange = { changes++ })
        item(THREE[1]).assertIsNotEnabled()
        item(THREE[1]).performClick()
        assertEquals(0, changes)
        item(THREE[2]).performClick()
        assertEquals(1, changes)
    }

    @Test
    fun disabledOptionShowsNoPress() {
        setControl(THREE, autoAdvance = false, disabled = setOf(1), keepSelection = true)
        val before = borderPixel(THREE[1])
        item(THREE[1]).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(300)
        assertColorNear("disabled border", before, borderPixel(THREE[1]))
        assertColorNear("disabled surface", before, surfacePixel(THREE[1]))
    }

    @Test
    fun disabledControlIgnoresAllTaps() {
        var changes = 0
        setControl(THREE, enabled = false, onChange = { changes++ })
        THREE.forEach { label ->
            item(label).assertIsNotEnabled()
            item(label).performClick()
        }
        assertEquals(0, changes)
    }

    @Test
    fun disabledSelectedItemCoversIndicatorWithBgDisabled() {
        setControl(THREE, enabled = false)
        assertDisabledCover()
    }

    @Test
    fun disabledSelectedOptionCoversIndicatorWithBgDisabled() {
        setControl(THREE, disabled = setOf(0))
        assertDisabledCover()
    }

    private fun assertDisabledCover() {
        assertColorNear("disabled selected surface", bgDisabled.compositeOver(LightPalette.gray00), surfacePixel(THREE[0]))
        assertColorNear("disabled selected border", strokeMuted.compositeOver(bgDisabled), borderPixel(THREE[0]))
    }

    private fun setControl(
        labels: List<String>,
        initial: Int = 0,
        modifier: Modifier = Modifier,
        autoAdvance: Boolean = true,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
        fontScale: Float = 1f,
        enabled: Boolean = true,
        disabled: Set<Int> = emptySet(),
        keepSelection: Boolean = false,
        onChange: (Int) -> Unit = {},
        container: @Composable (content: @Composable () -> Unit) -> Unit = { it() },
    ) {
        selected = initial
        composeRule.mainClock.autoAdvance = autoAdvance
        composeRule.setContent {
            val current = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(current.density, fontScale),
                LocalLayoutDirection provides layoutDirection,
            ) {
                PushlogTheme(darkTheme = false) {
                    weakPressed = PushlogTheme.color.bg.neutralWeakPressed
                    strokeMuted = PushlogTheme.color.stroke.neutralMuted
                    bgDisabled = PushlogTheme.color.bg.disabled
                    Box(Modifier.background(Color.White)) {
                        container {
                            PushlogSegmentedControl(
                                options = labels.mapIndexed { index, label -> PushlogSegmentedControlOption(label, index !in disabled) },
                                selectedIndex = selected,
                                onSelectedIndexChange = {
                                    // 놓을 때 선택이 바뀌면 indicator가 와서 눌림 해제 색을 볼 수 없다.
                                    if (!keepSelection) selected = it
                                    onChange(it)
                                },
                                modifier = modifier.testTag(CONTROL),
                                enabled = enabled,
                            )
                        }
                    }
                }
            }
        }
        if (autoAdvance) composeRule.waitForIdle() else composeRule.mainClock.advanceTimeByFrame()
    }

    private fun control() = composeRule.onNodeWithTag(CONTROL)

    private fun item(label: String) = composeRule.onNode(hasText(label) and hasAnyAncestor(hasTestTag(CONTROL)))

    private fun SemanticsNodeInteraction.bounds(): Rect = fetchSemanticsNode().boundsInRoot

    private fun capture(): PixelMap = composeRule.onRoot().captureToImage().toPixelMap()

    private fun isWhite(color: Color): Boolean = color.red * 255 >= 253 && color.green * 255 >= 253 && color.blue * 255 >= 253

    private fun borderPx(): Int = (1 * density).roundToInt()

    /** 컨트롤 왼쪽 끝에서 잰 indicator 왼쪽 끝(px). [y] 줄(기본은 세로 가운데)에서 indicator 첫 흰 픽셀을 찾아 테두리 1dp를 뺀다. */
    private fun indicatorStart(
        y: Int =
            control()
                .bounds()
                .center.y
                .toInt(),
    ): Int {
        val image = capture()
        val bounds = control().bounds()
        val left = bounds.left.roundToInt()
        // 컨테이너 모서리가 휜 줄에서는 왼쪽 끝이 바탕 흰색이다. 흰색이 아닌 첫 픽셀(테두리나 컨테이너) 뒤의 첫 흰 픽셀을 찾는다.
        val drawn = (left until bounds.right.toInt()).first { !isWhite(image[it, y]) }
        val x = (drawn until bounds.right.toInt()).first { isWhite(image[it, y]) }
        return x - borderPx() - left
    }

    /** indicator 오른쪽 끝의 루트 x(px). 세로 가운데 줄에서 오른쪽부터 찾은 첫 흰 픽셀 바로 뒤에 테두리 1dp를 더한다. */
    private fun indicatorEnd(): Int {
        val image = capture()
        val bounds = control().bounds()
        val y = bounds.center.y.toInt()
        val x = (bounds.right.toInt() - 1 downTo bounds.left.roundToInt()).first { isWhite(image[it, y]) }
        return x + 1 + borderPx()
    }

    /** 가운데 칸의 가로 가운데 열에서 잰 indicator 위·아래 끝의 루트 y(px). 라벨 픽셀은 흰 범위 안쪽에만 있다. */
    private fun indicatorVerticalRange(): Pair<Int, Int> {
        val image = capture()
        val bounds = control().bounds()
        val x = bounds.center.x.toInt()
        val rows = (bounds.top.roundToInt() until bounds.bottom.toInt()).filter { isWhite(image[x, it]) }
        return (rows.first() - borderPx()) to (rows.last() + 1 + borderPx())
    }

    /** 항목 표면 테두리 픽셀. 항목 가로 가운데, 컨트롤 위에서 (4 + 0.5)dp. */
    private fun borderPixel(label: String): Color = pixelAt(label, 4.5f)

    /** 항목 표면(테두리 안쪽) 픽셀. 항목 가로 가운데, 컨트롤 위에서 (4 + 3)dp. 라벨보다 위다. */
    private fun surfacePixel(label: String): Color = pixelAt(label, 7f)

    private fun pixelAt(
        label: String,
        dpFromControlTop: Float,
    ): Color {
        val x =
            item(label)
                .bounds()
                .center.x
                .toInt()
        val y = (control().bounds().top + dpFromControlTop * density).toInt()
        return capture()[x, y]
    }

    /**
     * 항목 안 라벨 글자 픽셀의 가로 범위(px, 끝 포함). 표면 색(위에서 (4 + 3)dp)과 많이 다른 픽셀을 글자로 본다.
     * 위아래 8dp는 테두리·둥근 모서리라 빼고 찾는다.
     */
    private fun labelRange(label: String): Pair<Int, Int> {
        val image = capture()
        val bounds = item(label).bounds()
        val surface = surfacePixel(label)
        val margin = (8 * density).roundToInt()
        val xs =
            (bounds.left.roundToInt() + margin until bounds.right.toInt() - margin).filter { x ->
                (bounds.top.roundToInt() + margin until bounds.bottom.toInt() - margin).any { y ->
                    colorDistance(image[x, y], surface) > LABEL_THRESHOLD
                }
            }
        return if (xs.isEmpty()) 0 to 0 else xs.first() to xs.last()
    }

    private fun colorDistance(
        a: Color,
        b: Color,
    ): Float = maxOf(abs(a.red - b.red), abs(a.green - b.green), abs(a.blue - b.blue))

    private fun assertColorNear(
        message: String,
        expected: Color,
        actual: Color,
    ) {
        assertTrue("$message: $actual should be within 2/255 of $expected", colorDistance(expected, actual) * 255 <= 2.5f)
    }

    private fun assertNear(
        message: String,
        expected: Float,
        actual: Float,
        tolerance: Float = 1f,
    ) {
        assertTrue("$message: $actual should be within ${tolerance}px of $expected", abs(actual - expected) <= tolerance)
    }

    private companion object {
        const val CONTROL = "control"
        const val NATURAL = "natural"
        const val LABEL_THRESHOLD = 0.2f
        val THREE = listOf("첫째", "둘째", "셋째")

        fun opt(label: String) = PushlogSegmentedControlOption(label)
    }
}
