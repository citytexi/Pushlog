package com.citytexi.pushlog.core.designsystem.component.searchinput

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** 실제 [AccessibilityNodeInfo]에 검색 이름이 닿는지 본다. 노드 정보가 TalkBack이 읽는 값이다. */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w400dp-h800dp")
class PushlogSearchInputAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun nameReachesNodeInfoWhenEmptyWithPlaceholder() {
        show(TextFieldState(), placeholder = PLACEHOLDER)
        val texts = spokenTexts()
        assertTrue("$texts", texts.any { it.contains(SEARCH) })
        assertTrue("$texts", texts.any { it.contains(PLACEHOLDER) })
        assertNull(inputInfo().contentDescription)
    }

    @Test
    fun nameReachesNodeInfoWithValue() {
        show(TextFieldState("커피"), placeholder = null)
        val texts = spokenTexts()
        assertTrue("$texts", texts.any { it.contains(SEARCH) })
        assertNull(inputInfo().contentDescription)
    }

    private fun show(
        state: TextFieldState,
        placeholder: String?,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogSearchInput(state = state, onSubmit = {}, placeholder = placeholder)
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

    private fun inputNode(): SemanticsNode =
        composeRule.onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.SetText), useUnmergedTree = true).fetchSemanticsNode()

    private fun inputInfo(): AccessibilityNodeInfo = requireNotNull(info(inputNode().id))

    /** 입력 노드와 접근성에 실제로 올라간 자손 노드의 text·contentDescription·hint. */
    private fun spokenTexts(): List<String> =
        descendantIds(inputNode())
            .mapNotNull(::info)
            .flatMap { listOf(it.text, it.contentDescription, it.hintText) }
            .mapNotNull { it?.toString() }

    private fun descendantIds(node: SemanticsNode): List<Int> = listOf(node.id) + node.children.flatMap(::descendantIds)

    private companion object {
        const val SEARCH = "검색"
        const val PLACEHOLDER = "가게 이름"
    }
}
