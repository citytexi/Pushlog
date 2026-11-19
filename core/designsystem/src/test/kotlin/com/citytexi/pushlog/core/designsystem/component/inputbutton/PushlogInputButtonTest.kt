package com.citytexi.pushlog.core.designsystem.component.inputbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.math.abs

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogInputButtonTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val clear get() = composeRule.onNodeWithContentDescription(CLEAR)
    private val button get() = composeRule.onNodeWithTag(BUTTON)
    private val surface get() = composeRule.onNode(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button) and isNotClear)

    // 위층 글자는 clearAndSetSemantics로 숨겨 글자로 찾을 수 없다. 숨김 노드를 그려진 순서대로 찾는다.
    private val hiddenTexts: SemanticsNodeInteractionCollection
        get() = composeRule.onAllNodes(SemanticsMatcher("clears semantics") { it.config.isClearingSemantics }, useUnmergedTree = true)

    private val isNotClear =
        SemanticsMatcher("not clear button") {
            it.config.getOrNull(SemanticsProperties.ContentDescription) !=
                listOf(CLEAR)
        }

    private var colors = Colors()

    private data class Colors(
        val transparentPressed: Color = Color.Unspecified,
        val disabled: Color = Color.Unspecified,
        val neutral: Color = Color.Unspecified,
        val critical: Color = Color.Unspecified,
    )

    private fun show(content: @Composable () -> Unit) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                val color = PushlogTheme.color
                colors = Colors(color.bg.transparentPressed, color.bg.disabled, color.fg.neutral, color.stroke.criticalSolid)
                Box(Modifier.background(Color.White)) { content() }
            }
        }
    }

    private fun showButton(
        value: String? = "1990.01.01",
        onClick: () -> Unit = {},
        onClear: (() -> Unit)? = null,
        enabled: Boolean = true,
        readOnly: Boolean = false,
        invalid: Boolean = false,
        placeholder: String? = null,
    ) {
        show {
            PushlogInputButton(
                value = value,
                onClick = onClick,
                modifier = Modifier.testTag(BUTTON).width(280.dp),
                placeholder = placeholder,
                onClear = onClear,
                contentDescription = NAME,
                invalid = invalid,
                enabled = enabled,
                readOnly = readOnly,
            )
        }
    }

    private fun capture(): PixelMap = button.captureToImage().toPixelMap()

    // 왼쪽 여백(x4) 가운데. 테두리와 내용이 닿지 않는 표면 빈 곳이다.
    private fun PixelMap.blankSpot(): Color = this[with(composeRule.density) { 8.dp.roundToPx() }, height / 2]

    @Test
    fun clickCallsOnClick() {
        var clicks = 0
        showButton(onClick = { clicks++ })
        surface.performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun clearCallsOnClearNotOnClick() {
        var clicks = 0
        var clears = 0
        showButton(onClick = { clicks++ }, onClear = { clears++ })
        clear.performClick()
        assertEquals(1, clears)
        assertEquals(0, clicks)
    }

    @Test
    fun clearTouchTargetIs44dpAndEdgeTapClears() {
        var clicks = 0
        var clears = 0
        showButton(onClick = { clicks++ }, onClear = { clears++ })
        clear.assertWidthIsEqualTo(44.dp).assertHeightIsEqualTo(44.dp)
        val edge = with(composeRule.density) { 2.dp.toPx() }
        clear.performTouchInput { click(Offset(edge, edge)) }
        assertEquals(1, clears)
        assertEquals(0, clicks)
    }

    @Test
    fun clearTouchPaddingKeepsIconGap() {
        showButton(onClear = {})
        val value = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        val target = clear.fetchSemanticsNode().boundsInRoot
        // 아이콘은 값에서 x2Half(10dp) 떨어지고, 탭 영역은 아이콘보다 11dp 넓다.
        val expected = with(composeRule.density) { value.right + (10.dp - 11.dp).toPx() }
        assertEquals(expected, target.left, 1f)
    }

    @Test
    fun pressingClearDoesNotPressSurface() {
        composeRule.mainClock.autoAdvance = false
        showButton(onClear = {})
        composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
        val before = capture().blankSpot()
        val valueBefore = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        clear.performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(200)
        assertEquals(before.toArgb(), capture().blankSpot().toArgb())
        assertEquals(Color.White.toArgb(), before.toArgb())
        val valueAfter = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        assertEquals(valueBefore.size, valueAfter.size)
    }

    @Test
    fun pressingSurfaceShrinksContentNotSurface() {
        composeRule.mainClock.autoAdvance = false
        showButton()
        composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
        val buttonBefore = button.fetchSemanticsNode().boundsInRoot
        val valueBefore = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        surface.performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(800)
        val valueAfter = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        assertTrue("value $valueBefore -> $valueAfter", valueAfter.width < valueBefore.width)
        assertEquals(buttonBefore, button.fetchSemanticsNode().boundsInRoot)
        assertEquals(colors.transparentPressed.compositeOver(Color.White).toArgb(), capture().blankSpot().toArgb())
    }

    @Test
    fun clearHiddenWhenNoValueOrNoOnClearOrDisabledOrReadOnly() {
        show {
            Column {
                PushlogInputButton(value = null, onClick = {}, onClear = {}, contentDescription = NAME)
                PushlogInputButton(value = "", onClick = {}, onClear = {}, contentDescription = NAME)
                PushlogInputButton(value = "값", onClick = {}, onClear = null, contentDescription = NAME)
                PushlogInputButton(value = "값", onClick = {}, onClear = {}, contentDescription = NAME, enabled = false)
                PushlogInputButton(value = "값", onClick = {}, onClear = {}, contentDescription = NAME, readOnly = true)
            }
        }
        clear.assertDoesNotExist()
    }

    @Test
    fun clearShownWhenValueAndOnClear() {
        showButton(onClear = {})
        clear.assertExists()
    }

    @Test
    fun emptyStringValueShowsPlaceholder() {
        showButton(value = "", placeholder = "선택")
        assertEquals(listOf("$NAME, 선택"), surfaceName())
    }

    @Test
    fun readOnlyIsNotClickableAndUsesDisabledBackground() {
        var clicks = 0
        showButton(value = SQUARE, onClick = { clicks++ }, readOnly = true)
        surface.performClick()
        assertEquals(0, clicks)
        val image = capture()
        assertEquals(colors.disabled.compositeOver(Color.White).toArgb(), image.blankSpot().toArgb())
        assertTrue("value glyph is not fg.neutral", image.contains(hiddenTexts[0].fetchSemanticsNode().boundsInRoot, colors.neutral))
    }

    @Test
    fun invalidDrawsCriticalStroke() {
        composeRule.mainClock.autoAdvance = false
        showButton(invalid = true)
        composeRule.mainClock.advanceTimeBy(200)
        capture()
        composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
        val image = capture()
        assertEquals(colors.critical.toArgb(), image[image.width / 2, 0].toArgb())
    }

    @Test
    fun semanticsNameIsLabelPlusValue() {
        show {
            PushlogField(label = "생일") { PushlogInputButton(value = "1990.01.01", onClick = {}) }
        }
        assertEquals(listOf("생일, 1990.01.01"), surfaceName())
        composeRule.onNodeWithText("1990.01.01", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithText("1990.01.01").assertDoesNotExist()
    }

    @Test
    fun contentDescriptionWinsOverFieldLabel() {
        show {
            PushlogField(label = "생일") {
                PushlogInputButton(value = "1990.01.01", onClick = {}, contentDescription = "생년월일")
            }
        }
        assertEquals(listOf("생년월일, 1990.01.01"), surfaceName())
    }

    @Test
    fun placeholderUsedInNameWhenNoValue() {
        showButton(value = null, placeholder = "날짜 선택")
        assertEquals(listOf("$NAME, 날짜 선택"), surfaceName())
        composeRule.onNodeWithText("날짜 선택", useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun nameIncludesPrefixValueSuffixInVisibleOrder() {
        show { PushlogInputButton(value = "1,000", onClick = {}, contentDescription = "가격", prefixText = "₩", suffixText = "원") }
        assertEquals(listOf("가격, ₩ 1,000 원"), surfaceName())
    }

    @Test
    fun nameIncludesOnlySuffix() {
        show { PushlogInputButton(value = "1,000", onClick = {}, contentDescription = "가격", suffixText = "원") }
        assertEquals(listOf("가격, 1,000 원"), surfaceName())
    }

    @Test
    fun nameOmitsAffixesWhenPlaceholderShown() {
        show {
            PushlogInputButton(
                value = null,
                onClick = {},
                placeholder = "금액",
                contentDescription = "가격",
                prefixText = "₩",
                suffixText = "원",
            )
        }
        assertEquals(listOf("가격, 금액"), surfaceName())
    }

    @Test
    fun inheritsFieldState() {
        var clicks = 0
        show {
            PushlogField(label = "생일", enabled = false) { PushlogInputButton(value = "1990.01.01", onClick = { clicks++ }) }
        }
        surface.performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun longValueEllipsizesAndKeepsSuffix() {
        show {
            PushlogInputButton(
                value = "가".repeat(200),
                onClick = {},
                modifier = Modifier.width(200.dp),
                contentDescription = NAME,
                suffixText = "원",
            )
        }
        val value = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        val suffix = hiddenTexts[1].fetchSemanticsNode().boundsInRoot
        val limit = with(composeRule.density) { (200.dp - 16.dp).toPx() }
        assertTrue("suffix right ${suffix.right} > $limit", suffix.right <= limit + 0.5f)
        assertTrue("value ${value.right} overlaps suffix ${suffix.left}", value.right <= suffix.left)
    }

    @Test
    fun rtlMirrorsPrefix() {
        composeRule.setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                PushlogTheme(darkTheme = false) {
                    PushlogInputButton(value = "값", onClick = {}, contentDescription = NAME, prefixText = "₩")
                }
            }
        }
        val prefix = hiddenTexts[0].fetchSemanticsNode().boundsInRoot
        val value = hiddenTexts[1].fetchSemanticsNode().boundsInRoot
        assertTrue("prefix ${prefix.left} value ${value.left}", prefix.left > value.left)
    }

    private fun surfaceName(): List<String>? = surface.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription)

    private fun PixelMap.contains(
        bounds: Rect,
        color: Color,
    ): Boolean {
        val origin = button.fetchSemanticsNode().boundsInRoot
        val target = color.compositeOver(Color.White)
        val left = (bounds.left - origin.left).toInt().coerceAtLeast(0)
        val right = (bounds.right - origin.left).toInt().coerceAtMost(width - 1)
        val top = (bounds.top - origin.top).toInt().coerceAtLeast(0)
        val bottom = (bounds.bottom - origin.top).toInt().coerceAtMost(height - 1)
        return (left..right).any { x -> (top..bottom).any { y -> this[x, y].near(target) } }
    }

    private fun Color.near(other: Color): Boolean =
        listOf(Color::red, Color::green, Color::blue).all { abs(it(this) - it(other)) <= CHANNEL_TOLERANCE }

    private companion object {
        const val BUTTON = "button"
        const val CLEAR = "지우기"
        const val NAME = "생일"
        const val SQUARE = "■■■"
        const val FRAME_MILLIS = 16L
        const val CHANNEL_TOLERANCE = 2f / 255f
    }
}
