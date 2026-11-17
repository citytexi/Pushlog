package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
class PushlogTextAreaTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val area get() = composeRule.onNodeWithTag(AREA)

    private fun height(): Dp =
        with(composeRule.density) {
            area
                .fetchSemanticsNode()
                .size.height
                .toDp()
        }

    private fun show(
        state: TextFieldState,
        lineLimits: TextFieldLineLimits = TextFieldLineLimits.MultiLine(),
        maxGraphemeCount: Int? = null,
        placeholder: String? = null,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogTextArea(
                    state,
                    modifier = Modifier.testTag(AREA).width(200.dp),
                    lineLimits = lineLimits,
                    maxGraphemeCount = maxGraphemeCount,
                    placeholder = placeholder,
                )
            }
        }
    }

    @Test
    fun longPlaceholderWrapsInsteadOfClipping() {
        val placeholder = "긴 안내 문구가 한 줄을 넘어가도 잘리지 않고 다음 줄로 이어져야 한다"
        show(TextFieldState(), placeholder = placeholder)
        val results = mutableListOf<TextLayoutResult>()
        composeRule
            .onNodeWithText(placeholder, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertTrue(results.single().lineCount > 1)
        assertFalse(results.single().hasVisualOverflow)
    }

    @Test
    fun minHeightIs94dp() {
        show(TextFieldState())
        assertEquals(94f, height().value, 0.5f)
    }

    @Test
    fun growsWithContent() {
        show(TextFieldState(FIVE_LINES))
        assertTrue(height() > 94.dp)
    }

    @Test
    fun respectsCallerHeightLimit() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column {
                    PushlogTextArea(TextFieldState(TEN_LINES), Modifier.testTag(AREA).width(200.dp))
                    PushlogTextArea(
                        TextFieldState(TEN_LINES),
                        Modifier.testTag(LIMITED).width(200.dp),
                        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3),
                    )
                }
            }
        }
        val limited =
            composeRule
                .onNodeWithTag(LIMITED)
                .fetchSemanticsNode()
                .size.height
        assertTrue(limited < area.fetchSemanticsNode().size.height)
    }

    @Test
    fun maxGraphemeCountApplies() {
        val state = TextFieldState()
        show(state, maxGraphemeCount = 5)
        area.performTextInput("가나다라마바사")
        assertEquals("가나다라마", state.text.toString())
    }

    private companion object {
        const val AREA = "area"
        const val LIMITED = "limited"
        const val FIVE_LINES = "1\n2\n3\n4\n5"
        const val TEN_LINES = "1\n2\n3\n4\n5\n6\n7\n8\n9\n10"
    }
}
