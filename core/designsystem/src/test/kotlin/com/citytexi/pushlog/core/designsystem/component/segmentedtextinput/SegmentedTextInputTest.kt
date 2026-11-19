package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performKeyInput
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.pressKey
import androidx.compose.ui.test.requestFocus
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
class SegmentedTextInputTest {
    @get:Rule
    val composeRule = createComposeRule()

    private fun card(
        state: PushlogCardNumberState,
        enabled: Boolean = true,
        readOnly: Boolean = false,
        layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    PushlogCardNumberInput(state, Modifier.width(400.dp), enabled = enabled, readOnly = readOnly)
                }
            }
        }
    }

    private fun resident(state: PushlogResidentNumberState) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) { PushlogResidentNumberInput(state, Modifier.width(400.dp)) }
        }
    }

    /** 칸 노드는 이름이 자식 글자로 합쳐지므로 그 글자와 편집 글자 유무로 찾는다(읽기 전용·비활성은 SetText가 없다). */
    private fun segment(label: String): SemanticsNodeInteraction = composeRule.onNode(hasText(label) and editable)

    private fun cardSegment(index: Int) = segment(CARD_LABELS[index])

    @Test
    fun fillingSegmentMovesFocusToNext() {
        val state = PushlogCardNumberState()
        card(state)
        repeat(4) { cardSegment(0).performTextInput("1") }
        assertEquals("1111", state.values[0])
        cardSegment(1).assertIsFocused()
    }

    @Test
    fun overLengthInsertInMiddleIsRejected() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "", "", ""))
        card(state)
        cardSegment(0).performTextInputSelection(TextRange(1))
        cardSegment(0).performTextInput("9")
        assertEquals("1234", state.values[0])
    }

    @Test
    fun backspaceOnEmptyDeletesPreviousLastChar() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "", "", ""))
        card(state)
        cardSegment(1).requestFocus()
        cardSegment(1).performKeyInput { pressKey(Key.Backspace) }
        assertEquals("123", state.values[0])
        cardSegment(0).assertIsFocused()
    }

    @Test
    fun arrowKeysMoveBetweenSegments() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "56", "", ""))
        card(state)
        cardSegment(0).requestFocus()
        cardSegment(0).performTextInputSelection(TextRange(4))
        cardSegment(0).performKeyInput { pressKey(Key.DirectionRight) }
        cardSegment(1).assertIsFocused()
        assertEquals(TextRange(0), state.fields.fields[1].selection)
        cardSegment(1).performKeyInput { pressKey(Key.DirectionLeft) }
        cardSegment(0).assertIsFocused()
        assertEquals(TextRange(4), state.fields.fields[0].selection)
    }

    @Test
    fun maskedEditsOnlyAtEnd() {
        val state = PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
        state.fields.setAll(listOf("", "123"))
        resident(state)
        val back = segment(RESIDENT_BACK_LABEL)
        back.requestFocus()
        back.performTextInputSelection(TextRange(0))
        composeRule.waitForIdle()
        assertEquals(TextRange(3), state.fields.fields[1].selection)
        back.performTextInput("4")
        assertEquals("1234", state.values[1])
    }

    @Test
    fun pasteDistributesAndStopsBeforeMasked() {
        val state = PushlogCardNumberState()
        card(state)
        cardSegment(0).performTextReplacement("1234-5678-9012-3456")
        composeRule.waitForIdle()
        assertEquals(listOf("1234", "5678", "", ""), state.values)
        // 다음 빈 칸은 가린 셋째 칸이다. 가린 칸은 사용자가 직접 친다.
        cardSegment(2).assertIsFocused()
    }

    @Test
    fun pasteIntoMaskedSegmentIsIgnored() {
        val state = PushlogCardNumberState()
        card(state)
        cardSegment(2).requestFocus()
        cardSegment(2).performTextReplacement("90123456")
        composeRule.waitForIdle()
        assertEquals("", state.values[2])
    }

    @Test
    fun birthDateAutofillIsNormalized() {
        val state = PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
        resident(state)
        segment(BIRTH_LABEL).performTextReplacement("1990-01-01")
        assertEquals("900101", state.values[0])
        segment(RESIDENT_BACK_LABEL).assertIsFocused()
    }

    @Test
    fun lastSegmentFullKeepsFocusAndDropsExtra() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "5678", "9012", "3456"))
        card(state)
        cardSegment(3).requestFocus()
        cardSegment(3).performTextInput("7")
        assertEquals("3456", state.values[3])
        cardSegment(3).assertIsFocused()
    }

    @Test
    fun disabledBlocksInput() {
        val state = PushlogCardNumberState()
        card(state, enabled = false)
        CARD_LABELS.forEach { segment(it).assertIsNotEnabled() }
        runCatching { cardSegment(0).performTextInput("1") }
        assertEquals(List(4) { "" }, state.values)
    }

    @Test
    fun readOnlyBlocksInputAndMovesButTakesFocus() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "", "", ""))
        card(state, readOnly = true)
        runCatching { cardSegment(1).performTextInput("5") }
        cardSegment(1).requestFocus()
        cardSegment(1).assertIsFocused()
        cardSegment(1).performKeyInput { pressKey(Key.Backspace) }
        assertEquals(listOf("1234", "", "", ""), state.values)
        cardSegment(1).assertIsFocused()
    }

    @Test
    fun maskedValueNotInSemantics() {
        val state = PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
        state.fields.setAll(listOf("900101", "1234567"))
        resident(state)
        assertMaskedHidden()
    }

    @Test
    fun autofillContentTypeOnlyOnVisibleSegments() {
        val resident = PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN)
        val card = PushlogCardNumberState()
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column {
                    PushlogResidentNumberInput(resident, Modifier.width(400.dp))
                    PushlogCardNumberInput(card, Modifier.width(400.dp))
                }
            }
        }
        assertEquals(ContentType.BirthDateFull, contentTypeOf(segment(BIRTH_LABEL)))
        assertEquals(ContentType.CreditCardNumber, contentTypeOf(cardSegment(0)))
        assertNull(contentTypeOf(cardSegment(1)))
        listOf(segment(RESIDENT_BACK_LABEL), cardSegment(2), cardSegment(3)).forEach { masked ->
            assertNull(contentTypeOf(masked))
            assertEquals(ContentDataType.None, masked.fetchSemanticsNode().config[SemanticsProperties.ContentDataType])
        }
    }

    @Test
    fun inheritsFieldStateAndShowsInvalidStroke() {
        composeRule.mainClock.autoAdvance = false
        var critical = Color.Unspecified
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                critical = PushlogTheme.color.stroke.criticalSolid
                PushlogField(invalid = true, errorMessage = "확인") {
                    PushlogCardNumberInput(PushlogCardNumberState(), Modifier.width(400.dp))
                }
            }
        }
        composeRule.mainClock.advanceTimeBy(200)
        val group = composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.Error))
        assertEquals("확인", group.fetchSemanticsNode().config[SemanticsProperties.Error])
        composeRule.onRoot().captureToImage()
        composeRule.mainClock.advanceTimeBy(FRAME_MILLIS)
        val image = group.captureToImage().toPixelMap()
        assertEquals(critical.toArgb(), image[image.width / 2, 0].toArgb())
    }

    @Test
    fun rtlKeepsSegmentOrder() {
        val state = PushlogCardNumberState()
        state.fields.setAll(listOf("1234", "", "", ""))
        card(state, layoutDirection = LayoutDirection.Rtl)
        val first = cardSegment(0).fetchSemanticsNode().boundsInRoot.left
        val second = cardSegment(1).fetchSemanticsNode().boundsInRoot.left
        assertTrue("$first < $second", first < second)
        cardSegment(1).requestFocus()
        cardSegment(1).performKeyInput { pressKey(Key.Backspace) }
        assertEquals("123", state.values[0])
        cardSegment(0).assertIsFocused()
    }

    @Test
    fun maskedStaysMaskedAfterRestore() {
        val tester = StateRestorationTester(composeRule)
        lateinit var first: PushlogResidentNumberState
        lateinit var second: PushlogResidentNumberState
        tester.setContent {
            PushlogTheme(darkTheme = false) {
                first = rememberResident()
                second = rememberResident()
                Column {
                    PushlogResidentNumberInput(first, Modifier.width(400.dp))
                    PushlogResidentNumberInput(second, Modifier.width(400.dp))
                }
            }
        }
        composeRule.runOnIdle {
            first.fields.setAll(listOf("900101", "1234567"))
            second.fields.setAll(listOf("900101", "9876543"))
        }
        tester.emulateSavedInstanceStateRestore()
        composeRule.runOnIdle {
            assertEquals("1234567", first.values[1])
            assertEquals("9876543", second.values[1])
        }
        assertMaskedHidden()
        // 숫자가 그림에 닿으면 값이 다른 두 칸의 픽셀이 달라진다.
        val backs = composeRule.onAllNodes(hasText(RESIDENT_BACK_LABEL) and editable)
        val a = backs[0].captureToImage().toPixelMap()
        val b = backs[1].captureToImage().toPixelMap()
        assertEquals(a.width, b.width)
        for (x in 0 until a.width) for (y in 0 until a.height) assertEquals(a[x, y], b[x, y])
    }

    @Composable
    private fun rememberResident(): PushlogResidentNumberState =
        rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN) }

    /** 가린 칸 값의 어떤 조각도 편집 글자·글자에 없고, 가린 칸에는 Password가 있다. */
    private fun assertMaskedHidden() {
        val nodes = composeRule.onAllNodes(SemanticsMatcher("any") { true }, useUnmergedTree = true).fetchSemanticsNodes()
        val texts =
            nodes.flatMap { node ->
                node.config
                    .getOrNull(SemanticsProperties.Text)
                    .orEmpty()
                    .map { it.text } +
                    listOfNotNull(node.config.getOrNull(SemanticsProperties.EditableText)?.text) +
                    node.config.getOrNull(SemanticsProperties.ContentDescription).orEmpty()
            }
        MASKED_FRAGMENTS.forEach { fragment -> assertFalse("$fragment in $texts", texts.any { it.contains(fragment) }) }
        val backs = composeRule.onAllNodes(hasText(RESIDENT_BACK_LABEL) and editable).fetchSemanticsNodes()
        assertTrue(backs.isNotEmpty())
        backs.forEach { back ->
            assertNotNull(back.config.getOrNull(SemanticsProperties.Password))
            assertEquals("•••••••", back.config[SemanticsProperties.EditableText].text)
        }
    }

    private fun contentTypeOf(node: SemanticsNodeInteraction): ContentType? =
        node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.ContentType)

    private companion object {
        val editable = SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText)
        val CARD_LABELS = (0 until 4).map { "카드번호 ${it * 4 + 1}-${(it + 1) * 4}번째 자리" }
        const val BIRTH_LABEL = "생년월일 6자리"
        const val RESIDENT_BACK_LABEL = "주민등록번호 뒷자리 7자리"
        val MASKED_FRAGMENTS = listOf("1234567", "1234", "9876543", "9876")
        const val FRAME_MILLIS = 16L
    }
}
