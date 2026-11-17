package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * 눌린 비선택·선택 항목과 이동이 끝난 indicator를 찍는다. 터치 포인터가 하나라 캡처마다 컨트롤 하나만 그린다.
 * 전환이 끝난 뒤 한 번만 찍으려고 시계를 멈추고 직접 흘린다.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogSegmentedControlScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private var selected by mutableIntStateOf(0)

    @Test
    fun pressedUnselectedLight() = capturePressed("segmented_control_pressed_unselected_light", darkTheme = false, label = LABELS[1])

    @Test
    fun pressedUnselectedDark() = capturePressed("segmented_control_pressed_unselected_dark", darkTheme = true, label = LABELS[1])

    @Test
    fun pressedSelectedLight() = capturePressed("segmented_control_pressed_selected_light", darkTheme = false, label = LABELS[0])

    @Test
    fun pressedSelectedDark() = capturePressed("segmented_control_pressed_selected_dark", darkTheme = true, label = LABELS[0])

    @Test
    fun movedLight() = captureMoved("segmented_control_moved_light", darkTheme = false)

    @Test
    fun movedDark() = captureMoved("segmented_control_moved_dark", darkTheme = true)

    private fun capturePressed(
        name: String,
        darkTheme: Boolean,
        label: String,
    ) {
        setControl(darkTheme)
        composeRule.onNode(hasText(label) and hasAnyAncestor(hasTestTag(CONTROL))).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun captureMoved(
        name: String,
        darkTheme: Boolean,
    ) {
        setControl(darkTheme)
        composeRule.runOnIdle { selected = 2 }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun setControl(darkTheme: Boolean) {
        selected = 0
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                Box(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) {
                    PushlogSegmentedControl(
                        options = LABELS.map { PushlogSegmentedControlOption(it) },
                        selectedIndex = selected,
                        // 눌린 모습만 찍으므로 선택은 바꾸지 않는다.
                        onSelectedIndexChange = {},
                        modifier = Modifier.testTag(CONTROL),
                    )
                }
            }
        }
        composeRule.mainClock.advanceTimeByFrame()
    }

    private companion object {
        const val CONTROL = "control"
        val LABELS = listOf("전체", "안 읽음", "중요")
    }
}
