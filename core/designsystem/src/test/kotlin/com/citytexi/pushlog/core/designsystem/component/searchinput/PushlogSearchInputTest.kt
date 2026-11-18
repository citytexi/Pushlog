package com.citytexi.pushlog.core.designsystem.component.searchinput

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotFocused
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.requestFocus
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogSearchInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val input get() = composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetText))
    private val clear get() = composeRule.onNodeWithContentDescription(CLEAR)

    private fun show(
        state: TextFieldState,
        submitted: MutableList<String> = mutableListOf(),
        clearFocusOnSubmit: Boolean = true,
        enabled: Boolean = true,
        modifier: Modifier = Modifier,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogSearchInput(
                    state = state,
                    onSubmit = { submitted += it },
                    modifier = modifier,
                    clearFocusOnSubmit = clearFocusOnSubmit,
                    enabled = enabled,
                )
            }
        }
    }

    @Test
    fun clearButtonOnlyWhenValue() {
        val state = TextFieldState()
        show(state)
        clear.assertDoesNotExist()
        input.performTextInput("a")
        clear.assertExists()
    }

    @Test
    fun whitespaceValueShowsClear() {
        val state = TextFieldState(" ")
        show(state)
        clear.assertExists().performClick()
        assertEquals("", state.text.toString())
    }

    @Test
    fun clearEmptiesValue() {
        val state = TextFieldState("커피")
        show(state)
        clear.performClick()
        assertEquals("", state.text.toString())
        clear.assertDoesNotExist()
    }

    @Test
    fun clearKeepsFocusWhenFocused() {
        val state = TextFieldState("커피")
        show(state)
        input.requestFocus()
        clear.performClick()
        input.assertIsFocused()
    }

    @Test
    fun clearDoesNotFocusWhenUnfocused() {
        val state = TextFieldState("커피")
        show(state)
        clear.performClick()
        input.assertIsNotFocused()
    }

    @Test
    fun submitCallsOnceWithValue() {
        val submitted = mutableListOf<String>()
        show(TextFieldState(), submitted)
        input.performTextInput("커피")
        input.performImeAction()
        assertEquals(listOf("커피"), submitted)
    }

    @Test
    fun submitEmptyCallsWithEmptyString() {
        val submitted = mutableListOf<String>()
        show(TextFieldState(), submitted)
        input.performImeAction()
        assertEquals(listOf(""), submitted)
    }

    @Test
    fun clearFocusOnSubmitTrueReleasesFocus() {
        show(TextFieldState("a"), clearFocusOnSubmit = true)
        input.requestFocus()
        input.performImeAction()
        input.assertIsNotFocused()
    }

    @Test
    fun clearFocusOnSubmitFalseKeepsFocus() {
        show(TextFieldState("a"), clearFocusOnSubmit = false)
        input.requestFocus()
        input.performImeAction()
        input.assertIsFocused()
    }

    @Test
    fun defaultLabels() {
        show(TextFieldState("a"))
        assertEquals(listOf("검색"), input.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription))
        assertEquals(listOf("지우기"), clear.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentDescription))
    }

    @Test
    fun ignoresFieldState() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(enabled = false, invalid = true, errorMessage = "오류") {
                    PushlogSearchInput(TextFieldState(), onSubmit = {})
                }
            }
        }
        input.assertIsEnabled()
        input.assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Error))
    }

    @Test
    fun disabledHidesClearAndRejectsInput() {
        val state = TextFieldState("a")
        show(state, enabled = false)
        composeRule.onNodeWithContentDescription("검색").assertIsNotEnabled()
        input.assertDoesNotExist()
        clear.assertDoesNotExist()
    }

    @Test
    fun longValueKeepsClearButtonVisible() {
        show(TextFieldState("가".repeat(200)), modifier = Modifier.width(200.dp))
        val right = clear.fetchSemanticsNode().boundsInRoot.right
        val limit = with(composeRule.density) { 200.dp.toPx() }
        assertTrue("clear right $right > $limit", right <= limit)
    }

    @Test
    fun rtlMirrorsClearButton() {
        composeRule.setContent {
            androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                PushlogTheme(darkTheme = false) {
                    PushlogSearchInput(TextFieldState("a"), onSubmit = {})
                }
            }
        }
        val clearLeft = clear.fetchSemanticsNode().boundsInRoot.left
        val inputLeft = input.fetchSemanticsNode().boundsInRoot.left
        assertTrue("clear $clearLeft input $inputLeft", clearLeft < inputLeft)
    }

    private companion object {
        const val CLEAR = "지우기"
    }
}
