package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
import androidx.compose.ui.unit.width
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
@Config(qualifiers = "w400dp-h800dp")
class PushlogSelectBoxGroupTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val columnsState = mutableIntStateOf(1)

    @Test
    fun groupIsSelectableGroup() {
        setGroup(columns = 2) { repeat(2) { Cell(it) } }
        val config = composeRule.onNodeWithTag(GROUP).fetchSemanticsNode().config
        assertTrue(config.contains(SemanticsProperties.SelectableGroup))
    }

    @Test
    fun columnsBelowRangeFails() {
        assertThrows(IllegalArgumentException::class.java) { setGroup(columns = 0) { Cell(0) } }
    }

    @Test
    fun columnsAboveRangeFails() {
        assertThrows(IllegalArgumentException::class.java) { setGroup(columns = 4) { Cell(0) } }
    }

    @Test
    fun cellsSplitWidthEquallyWithGaps() {
        setGroup(columns = 3) { repeat(3) { Cell(it) } }
        val cell = (GROUP_WIDTH - 12.dp * 2) / 3
        repeat(3) { assertDp(cell, bounds(it).width) }
        assertDp(12.dp, bounds(1).left - bounds(0).right)
    }

    @Test
    fun rowGapIsComponentDefault() {
        setGroup(columns = 2) { repeat(4) { Cell(it) } }
        assertDp(12.dp, bounds(2).top - bounds(0).bottom)
    }

    @Test
    fun rowsShareHeight() {
        setGroup(columns = 2) {
            Cell(0, description = THREE_LINES)
            Cell(1)
            Cell(2, footer = { Text("Footer\nsecond line\nthird line") })
            Cell(3)
        }
        assertDp(bounds(0).height, bounds(1).height)
        assertDp(bounds(2).height, bounds(3).height)
        assertTrue("footer adds height", bounds(2).height > bounds(1).height)
    }

    @Test
    fun singleColumnKeepsOwnHeights() {
        setGroup(columns = 1) {
            Cell(0, description = THREE_LINES)
            Cell(1)
        }
        assertTrue(bounds(0).height > bounds(1).height)
    }

    @Test
    fun defaultLayoutIsVerticalWithColumns() {
        columnsState.intValue = 2
        setGroup(columns = null) { Cell(0) }
        assertLabelInset(0, 16.dp)
        columnsState.intValue = 1
        composeRule.waitForIdle()
        assertLabelInset(0, 20.dp)
    }

    @Test
    fun boxLayoutOverridesGroup() {
        setGroup(columns = 2) {
            Cell(0, layout = PushlogSelectBoxLayout.HORIZONTAL)
            Cell(1)
        }
        assertLabelInset(0, 20.dp)
    }

    @Test
    fun partialLastRowKeepsCellWidth() {
        setGroup(columns = 3) { repeat(5) { Cell(it) } }
        assertDp(bounds(0).width, bounds(3).width)
        assertDp(bounds(1).left, bounds(4).left)
    }

    @Test
    fun emptyGroupHasNoHeight() {
        setGroup(columns = 2) {}
        assertEquals(
            0f,
            composeRule
                .onNodeWithTag(GROUP)
                .getBoundsInRoot()
                .height.value,
            0f,
        )
    }

    @Test
    fun largeFontInThreeColumnsKeepsLabelInsideCard() {
        setGroup(columns = 3, fontScale = 2f) { repeat(3) { Cell(it, label = "$LONG_LABEL $it") } }
        repeat(3) {
            val label = composeRule.onNodeWithText("$LONG_LABEL $it", useUnmergedTree = true).getBoundsInRoot()
            assertTrue("label $it bottom ${label.bottom} inside ${bounds(it).bottom}", label.bottom <= bounds(it).bottom)
            assertDp(bounds(0).height, bounds(it).height)
        }
    }

    @Test
    fun boxOutsideGroupWrapsHeightInBoundedColumn() {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                Column(Modifier.height(600.dp)) { Cell(0) }
            }
        }
        assertTrue("box ${bounds(0).height} should wrap its content", bounds(0).height < 200.dp)
    }

    /** [columns]가 null이면 [columnsState]를 읽어 그룹 열 수를 바꿀 수 있다. */
    private fun setGroup(
        columns: Int?,
        fontScale: Float = 1f,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale)) {
                    PushlogSelectBoxGroup(
                        modifier = Modifier.width(GROUP_WIDTH).testTag(GROUP),
                        columns = columns ?: columnsState.intValue,
                        content = content,
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Composable
    private fun Cell(
        index: Int,
        label: String = "$LABEL $index",
        description: String? = null,
        layout: PushlogSelectBoxLayout? = null,
        footer: (@Composable () -> Unit)? = null,
    ) {
        PushlogSelectBox(
            selected = footer != null,
            onClick = {},
            type = PushlogSelectBoxType.SINGLE,
            label = label,
            modifier = Modifier.testTag("b$index"),
            description = description,
            layout = layout,
            footerVisibility = PushlogSelectBoxFooterVisibility.ALWAYS,
            footer = footer,
        )
    }

    private fun bounds(index: Int): DpRect = composeRule.onNodeWithTag("b$index").getBoundsInRoot()

    private fun assertLabelInset(
        index: Int,
        expected: Dp,
    ) {
        val label = composeRule.onNodeWithText("$LABEL $index", useUnmergedTree = true).getBoundsInRoot()
        assertDp(expected, label.left - bounds(index).left)
    }

    /** 1px 안쪽이면 같다고 본다. */
    private fun assertDp(
        expected: Dp,
        actual: Dp,
    ) {
        val d = composeRule.density.density
        assertTrue("expected $expected, got $actual", abs((expected - actual).value * d) <= 1f)
    }

    private companion object {
        const val GROUP = "group"
        const val LABEL = "Box"
        const val LONG_LABEL = "아주 긴 선택지 이름"
        const val THREE_LINES = "first line of the description\nsecond line\nthird line"
        val GROUP_WIDTH = 360.dp
    }
}
