package com.citytexi.pushlog.core.designsystem.component.textinput

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** 실제 [AccessibilityNodeInfo]에 field 문구가 닿는지 본다. 노드 정보가 TalkBack이 읽는 값이다. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h800dp")
class PushlogTextInputAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun labelDescriptionAndExtrasReachNodeInfo() {
        show(invalid = false, textArea = false)
        val texts = spokenTexts()
        listOf(LABEL, DESCRIPTION, INDICATOR, "필수", "3/40자").forEach { expected ->
            assertTrue("$expected missing from node info", texts.any { it.contains(expected) })
        }
        val info = inputInfo()
        assertNull(info.contentDescription)
        assertNull(info.stateDescription)
        assertFalse(info.isContentInvalid)
    }

    @Test
    fun invalidSetsErrorOnInputNode() {
        show(invalid = true, textArea = false)
        val info = inputInfo()
        assertTrue(info.isContentInvalid)
        assertEquals(ERROR, info.error?.toString())
        assertTrue(spokenTexts().any { it.contains(DESCRIPTION) })
    }

    @Test
    fun textAreaGetsSameTexts() {
        show(invalid = false, textArea = true)
        val texts = spokenTexts()
        assertTrue(texts.any { it.contains(LABEL) })
        assertTrue(texts.any { it.contains(DESCRIPTION) })
    }

    private fun show(
        invalid: Boolean,
        textArea: Boolean,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(
                    label = LABEL,
                    showRequiredIndicator = true,
                    indicatorText = INDICATOR,
                    description = DESCRIPTION,
                    errorMessage = ERROR,
                    characterCount = 3,
                    maxCharacterCount = 40,
                    invalid = invalid,
                ) {
                    if (textArea) {
                        PushlogTextArea(TextFieldState(), modifier = Modifier.testTag(INPUT))
                    } else {
                        PushlogTextInput(TextFieldState(), modifier = Modifier.testTag(INPUT))
                    }
                }
            }
        }
        composeRule.waitForIdle()
    }

    private fun composeView(): View {
        var view: View = composeRule.activity.findViewById<ViewGroup>(android.R.id.content)
        while (view is ViewGroup && view.javaClass.simpleName != "AndroidComposeView") view = view.getChildAt(0)
        return view
    }

    private fun info(id: Int): AccessibilityNodeInfo? = composeView().accessibilityNodeProvider.createAccessibilityNodeInfo(id)

    private fun inputNode(): SemanticsNode = composeRule.onNodeWithTag(INPUT, useUnmergedTree = true).fetchSemanticsNode()

    private fun inputInfo(): AccessibilityNodeInfo = requireNotNull(info(inputNode().id))

    /** 입력 노드와 접근성에 실제로 올라간 자손 노드의 text·contentDescription·hint. */
    private fun spokenTexts(): List<String> =
        descendantIds(inputNode())
            .mapNotNull(::info)
            .flatMap { listOf(it.text, it.contentDescription, it.hintText) }
            .mapNotNull { it?.toString() }

    private fun descendantIds(node: SemanticsNode): List<Int> = listOf(node.id) + node.children.flatMap(::descendantIds)

    private companion object {
        const val INPUT = "input"
        const val LABEL = "이름"
        const val DESCRIPTION = "실명을 입력하세요"
        const val INDICATOR = "선택"
        const val ERROR = "필수 항목"
    }
}
