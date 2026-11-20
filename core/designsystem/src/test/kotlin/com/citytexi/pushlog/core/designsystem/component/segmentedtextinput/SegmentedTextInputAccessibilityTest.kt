package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
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

/** 실제 [AccessibilityNodeInfo]에 칸 이름이 닿는지 본다. placeholder가 이름 자식을 다 덮으면 그 자식이 트리에서 빠진다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp")
class SegmentedTextInputAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emptyCellsWithPlaceholderStillExposeLabel() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column {
                    PushlogCardNumberInput(PushlogCardNumberState(), Modifier.width(400.dp))
                    PushlogResidentNumberInput(PushlogResidentNumberState(PushlogResidentNumberDigits.SEVEN), Modifier.width(400.dp))
                }
            }
        }
        composeRule.waitForIdle()
        val cells = inputNodes()
        assertEquals(6, cells.size)
        val labels =
            listOf(
                "카드번호 1-4번째 자리",
                "카드번호 5-8번째 자리",
                "카드번호 9-12번째 자리",
                "카드번호 13-16번째 자리",
                "생년월일 6자리",
                "주민등록번호 뒷자리 첫 번째 자리",
            )
        cells.zip(labels).forEach { (cell, label) ->
            val texts = spokenTexts(cell)
            assertTrue("$label not in $texts", texts.any { it.contains(label) })
        }
    }

    @Test
    fun fieldLabelOnFirstCellAndErrorOnEveryCell() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogField(label = "결제 카드", invalid = true, errorMessage = "확인") {
                    PushlogCardNumberInput(PushlogCardNumberState(), Modifier.width(400.dp))
                }
            }
        }
        composeRule.waitForIdle()
        val cells = inputNodes()
        assertEquals(4, cells.size)
        assertTrue(spokenTexts(cells[0]).any { it.startsWith("결제 카드") })
        cells.drop(1).forEach { cell -> assertFalse(spokenTexts(cell).any { it.contains("결제 카드") }) }
        cells.forEach { cell ->
            val info = info(cell.id)
            assertTrue(info?.isContentInvalid == true)
            assertEquals("확인", info?.error?.toString())
        }
    }

    private fun composeView(): View {
        var view: View = composeRule.activity.findViewById<ViewGroup>(android.R.id.content)
        while (view is ViewGroup && view.javaClass.simpleName != "AndroidComposeView") view = view.getChildAt(0)
        return view
    }

    private fun info(id: Int): AccessibilityNodeInfo? = composeView().accessibilityNodeProvider.createAccessibilityNodeInfo(id)

    private fun inputNodes(): List<SemanticsNode> =
        composeRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .sortedWith(compareBy({ it.boundsInRoot.top }, { it.boundsInRoot.left }))

    /** 입력 노드에서 노드 정보의 자식 목록을 따라 닿는 노드의 text·contentDescription·hint. 덮인 자식은 자식 목록에서 빠진다. */
    private fun spokenTexts(node: SemanticsNode): List<String> =
        reachableInfos(node.id)
            .flatMap { listOf(it.text, it.contentDescription, it.hintText) }
            .mapNotNull { it?.toString() }

    private fun reachableInfos(id: Int): List<AccessibilityNodeInfo> {
        val info = info(id) ?: return emptyList()
        return listOf(info) + (0 until info.childCount).flatMap { reachableInfos(childVirtualId(info, it)) }
    }

    // getChildId는 숨은 API다. 값의 위 32비트가 가상 노드 id다.
    private fun childVirtualId(
        info: AccessibilityNodeInfo,
        index: Int,
    ): Int = ((getChildId.invoke(info, index) as Long) shr VIRTUAL_ID_SHIFT).toInt()

    private companion object {
        const val VIRTUAL_ID_SHIFT = 32
        val getChildId = AccessibilityNodeInfo::class.java.getDeclaredMethod("getChildId", Int::class.javaPrimitiveType)
    }
}
