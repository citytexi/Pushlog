package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.height
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
@Config(qualifiers = "w600dp-h600dp")
class PushlogSelectBoxFooterTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val selectedState = mutableStateOf(false)
    private var clicks = 0

    @Test
    fun whenSelectedShowsOnlySelected() {
        setBox(PushlogSelectBoxFooterVisibility.WHEN_SELECTED)
        assertFooterShownFor(selected = true, expected = true)
        assertFooterShownFor(selected = false, expected = false)
    }

    @Test
    fun alwaysShowsInBothStates() {
        setBox(PushlogSelectBoxFooterVisibility.ALWAYS)
        assertFooterShownFor(selected = true, expected = true)
        assertFooterShownFor(selected = false, expected = true)
    }

    @Test
    fun whenNotSelectedShowsOnlyUnselected() {
        setBox(PushlogSelectBoxFooterVisibility.WHEN_NOT_SELECTED)
        assertFooterShownFor(selected = true, expected = false)
        assertFooterShownFor(selected = false, expected = true)
    }

    @Test
    fun footerOpensOver400msAndClosesOver300ms() {
        composeRule.mainClock.autoAdvance = false
        setBox(PushlogSelectBoxFooterVisibility.WHEN_SELECTED)
        val closed = settledHeight(selected = false)
        val open = settledHeight(selected = true)
        settledHeight(selected = false)

        switchTo(selected = true)
        composeRule.mainClock.advanceTimeBy(200)
        assertBetween(closed, open, height())
        // 여는 높이가 300ms라면 여기서 이미 다 열렸다. 400ms면 easing으로 94%쯤이라 footer가 세 줄이어야 차이가 보인다.
        composeRule.mainClock.advanceTimeBy(100)
        assertBetween(closed, open, height())
        composeRule.mainClock.advanceTimeBy(150)
        assertEquals(open, height())

        switchTo(selected = false)
        composeRule.mainClock.advanceTimeBy(150)
        assertBetween(closed, open, height())
        composeRule.mainClock.advanceTimeBy(200)
        assertEquals(closed, height())
        // 높이는 300ms에 닫혀도 투명도는 400ms까지 이어진다. 그 뒤에야 footer가 빠진다.
        footer().assertExists()
        composeRule.mainClock.advanceTimeBy(100)
        footer().assertDoesNotExist()
    }

    @Test
    fun footerReversesMidAnimation() {
        composeRule.mainClock.autoAdvance = false
        setBox(PushlogSelectBoxFooterVisibility.WHEN_SELECTED)
        val closed = settledHeight(selected = false)

        switchTo(selected = true)
        composeRule.mainClock.advanceTimeBy(150)
        switchTo(selected = false)
        composeRule.mainClock.advanceTimeBy(500)
        footer().assertDoesNotExist()
        assertEquals(closed, height())
    }

    @Test
    fun footerIsInsideCardTapTarget() {
        selectedState.value = true
        setBox(PushlogSelectBoxFooterVisibility.WHEN_SELECTED)
        footer().assertExists()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(1)
        footer().performClick()
        assertEquals(1, clicks)
    }

    private fun setBox(visibility: PushlogSelectBoxFooterVisibility) {
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                PushlogSelectBox(
                    selected = selectedState.value,
                    onClick = { clicks++ },
                    type = PushlogSelectBoxType.MULTIPLE,
                    label = LABEL,
                    modifier = Modifier.width(300.dp).testTag(TAG),
                    footerVisibility = visibility,
                    footer = { Text(FOOTER) },
                )
            }
        }
        composeRule.waitForIdle()
    }

    private fun assertFooterShownFor(
        selected: Boolean,
        expected: Boolean,
    ) {
        composeRule.runOnIdle { selectedState.value = selected }
        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)
        if (expected) footer().assertExists() else footer().assertDoesNotExist()
    }

    /**
     * 상태를 바꾸고 애니메이션이 시작되는 프레임까지 흘린다. 이후 시각은 이 프레임 기준이다.
     * 시계를 멈춘 동안에는 상태 변경 알림이 루퍼에 머물러 있어, 프레임을 내기 전에 루퍼를 비운다.
     */
    private fun switchTo(selected: Boolean) {
        composeRule.runOnIdle { selectedState.value = selected }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
    }

    private fun settledHeight(selected: Boolean): Dp {
        switchTo(selected)
        composeRule.mainClock.advanceTimeBy(SETTLE_MILLIS)
        return height()
    }

    private fun height(): Dp = composeRule.onNodeWithTag(TAG).getBoundsInRoot().height

    private fun footer() = composeRule.onNodeWithText(FOOTER, useUnmergedTree = true)

    private fun assertBetween(
        closed: Dp,
        open: Dp,
        actual: Dp,
    ) {
        assertTrue("$closed < $actual < $open", actual > closed && actual < open)
    }

    private companion object {
        const val TAG = "box"
        const val LABEL = "사과"
        const val FOOTER = "추가 정보\n둘째 줄\n셋째 줄"
        const val SETTLE_MILLIS = 1000L
    }
}
