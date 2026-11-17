package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogTextInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val input get() = composeRule.onNodeWithTag(INPUT)

    @Test
    fun maxGraphemeCountBlocksExtraInput() {
        val state = TextFieldState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 3) }
        }
        input.performTextInput("가나다라")
        assertEquals("가나다", state.text.toString())
    }

    @Test
    fun pasteLongerThanMaxTruncatesAtGraphemeBoundary() {
        val state = TextFieldState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 2) }
        }
        input.performTextReplacement("a👨‍👩‍👧b")
        assertEquals("a👨‍👩‍👧", state.text.toString())
    }

    @Test
    fun disabledRejectsInput() {
        val state = TextFieldState()
        composeRule.setContent {
            PushlogTheme(
                darkTheme = false,
            ) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), enabled = false) }
        }
        input.assertIsNotEnabled()
        runCatching { input.performTextInput("abc") }
        assertEquals("", state.text.toString())
    }

    @Test
    fun readOnlyKeepsValue() {
        val state = TextFieldState("abc")
        composeRule.setContent {
            PushlogTheme(
                darkTheme = false,
            ) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), readOnly = true) }
        }
        runCatching { input.performTextInput("d") }
        assertEquals("abc", state.text.toString())
        assertFalse(input.fetchSemanticsNode().config[SemanticsProperties.IsEditable])
    }

    @Test
    fun readOnlyAllowsSelectionAndCopy() {
        val state = TextFieldState("abc")
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), readOnly = true) }
        }
        input.assert(SemanticsMatcher.keyIsDefined(SemanticsActions.SetSelection))
        input.performTextInputSelection(TextRange(0, 3))
        input.assert(SemanticsMatcher.keyIsDefined(SemanticsActions.CopyText))
        assertEquals("abc", state.text.toString())
        assertEquals(TextRange(0, 3), state.selection)
    }

    @Test
    fun inheritsFieldStateAndArgumentWins() {
        val state = TextFieldState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(invalid = true, enabled = false, label = "이름", errorMessage = "오류") {
                    PushlogTextInput(state, modifier = Modifier.testTag(INPUT))
                }
            }
        }
        input.assertIsNotEnabled()
        val config = input.fetchSemanticsNode().config
        assertEquals("오류", config[SemanticsProperties.Error])
        assertEquals(listOf("이름"), config[SemanticsProperties.Text].map { it.text })
        assertNull(config.getOrNull(SemanticsProperties.ContentDescription))
    }

    @Test
    fun explicitEnabledOverridesFieldState() {
        val state = TextFieldState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), enabled = true) }
            }
        }
        input.assertIsEnabled()
    }

    @Test
    fun noFieldErrorWhenNotInvalid() {
        composeRule.setContent {
            PushlogTheme(
                darkTheme = false,
            ) { PushlogTextInput(TextFieldState(), modifier = Modifier.testTag(INPUT)) }
        }
        assertNull(input.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Error))
    }

    @Test
    fun invalidClearedWhileFocusedKeepsEmphasis() {
        val (mid, end) = strokeWhileFocused(initialInvalid = true)
        // 오류 층이 걷히는 동안 포커스 층이 아래에 그대로 있어 두 색 사이에 머문다(투명해지지 않는다).
        assertBetween(mid, contrast, critical)
        assertEquals(contrast.toArgb(), end.toArgb())
    }

    @Test
    fun invalidSetWhileFocusedFadesErrorIn() {
        val (mid, end) = strokeWhileFocused(initialInvalid = false)
        assertNotEquals(critical.toArgb(), mid.toArgb())
        assertBetween(mid, contrast, critical)
        assertEquals(critical.toArgb(), end.toArgb())
    }

    private var contrast = Color.Unspecified
    private var critical = Color.Unspecified

    /** 포커스한 입력의 invalid를 뒤집고 전환 중간(약 66ms)과 끝(약 232ms)의 위쪽 테두리 색. */
    private fun strokeWhileFocused(initialInvalid: Boolean): Pair<Color, Color> {
        composeRule.mainClock.autoAdvance = false
        var invalid by mutableStateOf(initialInvalid)
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                contrast = PushlogTheme.color.stroke.neutralContrast
                critical = PushlogTheme.color.stroke.criticalSolid
                PushlogTextInput(TextFieldState(), modifier = Modifier.testTag(INPUT).width(200.dp), invalid = invalid)
            }
        }
        composeRule.onNodeWithTag(INPUT).performClick()
        composeRule.mainClock.advanceTimeBy(200)
        composeRule.runOnIdle { invalid = !initialInvalid }
        return listOf(50L, 150L)
            .map { elapsed ->
                composeRule.mainClock.advanceTimeBy(elapsed)
                // 멈춘 시계에서는 상태가 바뀐 뒤 첫 캡처가 이전 프레임을 돌려주므로 한 번 버린다.
                composeRule.onRoot().captureToImage()
                composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
                val image = composeRule.onRoot().captureToImage().toPixelMap()
                image[image.width / 2, 0]
            }.let { it[0] to it[1] }
    }

    private fun assertBetween(
        actual: Color,
        a: Color,
        b: Color,
    ) {
        listOf(Color::red, Color::green, Color::blue).forEach { channel ->
            val low = minOf(channel(a), channel(b)) - CHANNEL_TOLERANCE
            val high = maxOf(channel(a), channel(b)) + CHANNEL_TOLERANCE
            assertTrue("$actual not between $a and $b", channel(actual) in low..high)
        }
    }

    @Test
    fun tapOnPaddingFocusesInput() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogTextInput(TextFieldState(), modifier = Modifier.testTag(INPUT).width(200.dp))
            }
        }
        composeRule.onNodeWithTag(INPUT).performTouchInput { click(Offset(2f, centerY)) }
        input.assertIsFocused()
    }

    @Test
    fun typedValueIsKept() {
        val state = TextFieldState()
        composeRule.setContent { PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT)) } }
        input.performTextInput("hi")
        assertEquals("hi", state.text.toString())
    }

    @Test
    fun midInsertAtLimitKeepsExistingText() {
        val state = TextFieldState("abc", TextRange(0))
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 3) }
        }
        input.performTextInput("x")
        assertEquals("abc", state.text.toString())
    }

    @Test
    fun insertBeyondRemainingCapacityTrimsInsertedPart() {
        val state = TextFieldState("ab", TextRange(1))
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 4) }
        }
        input.performTextInput("XYZ")
        assertEquals("aXYb", state.text.toString())
    }

    @Test
    fun backspaceShrinksTextAlreadyOverLimit() {
        val state = TextFieldState("abcdef")
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 3) }
        }
        input.requestFocus()
        input.performKeyInput { pressKey(Key.Backspace) }
        assertEquals("abcde", state.text.toString())
    }

    @Test
    fun replacingSelectionOverLimitKeepsReplacedLength() {
        val state = TextFieldState("abcdef", TextRange(1, 2))
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 3) }
        }
        input.performTextInput("xy")
        assertEquals("axcdef", state.text.toString())
    }

    @Test
    fun replacingSelectionAtLimitTrimsInsertedPart() {
        val state = TextFieldState("abc", TextRange(1, 2))
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogTextInput(state, modifier = Modifier.testTag(INPUT), maxGraphemeCount = 3) }
        }
        input.performTextInput("xy")
        assertEquals("axc", state.text.toString())
    }

    private companion object {
        const val INPUT = "input"
        const val FRAME_MILLIS = 16L
        const val CHANNEL_TOLERANCE = 0.01f
    }
}
