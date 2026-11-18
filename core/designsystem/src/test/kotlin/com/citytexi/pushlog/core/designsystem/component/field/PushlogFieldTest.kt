package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogFieldTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var critical = 0
    private var subtle = 0

    // 안쪽 입력(Task 4 전)을 흉내 내는 노드.
    @Composable
    private fun FakeInput() {
        val field = LocalPushlogFieldState.current
        Box(
            Modifier
                .testTag(INPUT)
                .width(100.dp)
                .height(40.dp)
                .pushlogFieldSemantics(field, field.invalid),
        )
    }

    private fun input() = composeRule.onNodeWithTag(INPUT).fetchSemanticsNode().config

    @Test
    fun errorShownOnlyWhenInvalidAndDescriptionHiddenButReadable() {
        setField(invalid = true, errorMessage = ERROR, description = DESCRIPTION)
        composeRule.onNodeWithText(ERROR, useUnmergedTree = true).assertExists()
        composeRule.onNodeWithText(DESCRIPTION, useUnmergedTree = true).assertDoesNotExist()
        assertEquals(ERROR, input()[SemanticsProperties.Error])
        assertEquals(DESCRIPTION, input()[SemanticsProperties.StateDescription])
    }

    @Test
    fun descriptionShownWhenValid() {
        setField(invalid = false, errorMessage = ERROR, description = DESCRIPTION)
        composeRule.onNodeWithText(ERROR, useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithText(DESCRIPTION, useUnmergedTree = true).assertExists()
        assertEquals(null, input().getOrNull(SemanticsProperties.Error))
    }

    @Test
    fun errorMessageAppearsOnceInSemantics() {
        setField(invalid = true, errorMessage = ERROR, description = DESCRIPTION, label = LABEL)
        composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Error)).assertCountEquals(1)
        composeRule.onAllNodesWithText(ERROR).assertCountEquals(0)
        composeRule.onAllNodesWithText(LABEL).assertCountEquals(0)
    }

    @Test
    fun headerAndFooterOmittedWhenEmpty() {
        setField()
        val root =
            composeRule
                .onRoot()
                .fetchSemanticsNode()
                .size.height
        val box =
            composeRule
                .onNodeWithTag(INPUT)
                .fetchSemanticsNode()
                .size.height
        assertEquals(box, root)
    }

    @Test
    fun characterCountTurnsCriticalWhenInvalid() {
        setField(invalid = true, characterCount = 3, maxCharacterCount = 40)
        assertTrue("critical pixels expected", criticalPixels() > 0)
    }

    @Test
    fun characterCountNotCriticalWhenValid() {
        setField(invalid = false, characterCount = 3, maxCharacterCount = 40)
        assertEquals(0, criticalPixels())
    }

    @Test
    fun descriptionWithoutIconAlignsWithLabel() {
        setField(label = LABEL, description = DESCRIPTION)
        val labelLeft =
            composeRule
                .onNodeWithText(LABEL, useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.left
        val descLeft =
            composeRule
                .onNodeWithText(DESCRIPTION, useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.left
        assertEquals(labelLeft, descLeft, 0.5f)
    }

    @Test
    fun requiredIndicatorScalesWithFont() {
        var scale by mutableFloatStateOf(1f)
        setField(label = LABEL, showRequiredIndicator = true, fontScale = { scale })
        val one = indicatorWidth()
        scale = 2f
        composeRule.waitForIdle()
        val two = indicatorWidth()
        assertTrue("dot should be drawn, was $one", one > 0)
        assertTrue("dot at 2x ($two) should be about twice 1x ($one)", two in (one * 2 - 2)..(one * 2 + 2))
    }

    @Test
    fun requiredIndicatorDoesNotOverlapLabel() {
        setField(label = LABEL, showRequiredIndicator = true, fontScale = { 2f })
        val labelRight =
            composeRule
                .onNodeWithText(LABEL, useUnmergedTree = true)
                .fetchSemanticsNode()
                .boundsInRoot.right
        val dotLeft = criticalBounds().first
        assertTrue("dot left $dotLeft should not be before label right $labelRight", dotLeft >= labelRight)
    }

    @Test
    fun errorIconCenteredOnFirstTextLine() {
        var scale by mutableFloatStateOf(1f)
        setField(invalid = true, errorMessage = ERROR, fontScale = { scale })
        assertIconOnFirstLine(ERROR, critical, scale)
        scale = 2f
        composeRule.waitForIdle()
        assertIconOnFirstLine(ERROR, critical, scale)
    }

    @Test
    fun errorIconStaysOnFirstLineWhenMessageWraps() {
        setField(invalid = true, errorMessage = LONG_ERROR)
        assertIconOnFirstLine(LONG_ERROR, critical, 1f, minLines = 2)
    }

    @Test
    fun descriptionIconCenteredOnFirstTextLine() {
        var scale by mutableFloatStateOf(1f)
        setField(description = DESCRIPTION, descriptionIcon = ColorPainter(Color.Black), fontScale = { scale })
        assertIconOnFirstLine(DESCRIPTION, subtle, scale)
        scale = 2f
        composeRule.waitForIdle()
        assertIconOnFirstLine(DESCRIPTION, subtle, scale)
    }

    /** 글자 왼쪽에서 [color] 픽셀이 차지하는 세로 범위의 가운데가 첫 줄 가운데와 1px 안에 있어야 한다. */
    private fun assertIconOnFirstLine(
        text: String,
        color: Int,
        scale: Float,
        minLines: Int = 1,
    ) {
        val node = composeRule.onNodeWithText(text, useUnmergedTree = true)
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val layouts = mutableListOf<TextLayoutResult>()
        node.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(layouts) }
        val layout = layouts.single()
        assertTrue("expected at least $minLines lines", layout.lineCount >= minLines)
        val lineCenter = bounds.top + (layout.getLineTop(0) + layout.getLineBottom(0)) / 2
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        var top = Int.MAX_VALUE
        var bottom = -1
        for (x in 0 until bounds.left.toInt()) {
            for (y in 0 until image.height) {
                if (image[x, y].toArgb() == color) {
                    top = minOf(top, y)
                    bottom = maxOf(bottom, y)
                }
            }
        }
        assertTrue("icon should be drawn at scale $scale", bottom >= 0)
        val iconCenter = (top + bottom + 1) / 2f
        assertEquals("icon center at scale $scale", lineCenter, iconCenter, 1f)
    }

    private fun indicatorWidth(): Int {
        val (left, right) = criticalBounds()
        return (right - left).toInt() + 1
    }

    private fun criticalPixels(): Int {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        var count = 0
        for (x in 0 until image.width) {
            for (y in 0 until image.height) if (image[x, y].toArgb() == critical) count++
        }
        return count
    }

    /** fg.critical 픽셀이 차지하는 가로 범위(left, right). */
    private fun criticalBounds(): Pair<Float, Float> {
        val image = composeRule.onRoot().captureToImage().toPixelMap()
        var left = Int.MAX_VALUE
        var right = -1
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                if (image[x, y].toArgb() == critical) {
                    if (x < left) left = x
                    if (x > right) right = x
                }
            }
        }
        return left.toFloat() to right.toFloat()
    }

    private fun setField(
        label: String? = null,
        showRequiredIndicator: Boolean = false,
        description: String? = null,
        descriptionIcon: Painter? = null,
        errorMessage: String? = null,
        characterCount: Int? = null,
        maxCharacterCount: Int? = null,
        invalid: Boolean = false,
        fontScale: () -> Float = { 1f },
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                critical =
                    PushlogTheme.color.fg.critical
                        .toArgb()
                subtle =
                    PushlogTheme.color.fg.neutralSubtle
                        .toArgb()
                CompositionLocalProvider(
                    LocalDensity provides Density(LocalDensity.current.density, fontScale()),
                ) {
                    PushlogField(
                        label = label,
                        showRequiredIndicator = showRequiredIndicator,
                        description = description,
                        descriptionIcon = descriptionIcon,
                        errorMessage = errorMessage,
                        characterCount = characterCount,
                        maxCharacterCount = maxCharacterCount,
                        invalid = invalid,
                    ) { FakeInput() }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private companion object {
        const val INPUT = "input"
        const val ERROR = "필수 항목"
        const val DESCRIPTION = "숫자만"
        const val LABEL = "이름"
        const val LONG_ERROR = "필수 항목입니다. 이름을 입력하지 않으면 다음 단계로 넘어갈 수 없으니 꼭 입력해 주세요. 입력한 이름은 기기 안에만 저장됩니다."
    }
}
