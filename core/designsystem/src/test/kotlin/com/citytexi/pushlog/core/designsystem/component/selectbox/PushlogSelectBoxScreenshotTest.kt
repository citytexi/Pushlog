package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogSelectBoxScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSources = List(CELL_COUNT) { MutableInteractionSource() }

    @Test
    fun pressedLight() = capturePressed("select_box_pressed_light", darkTheme = false)

    @Test
    fun pressedDark() = capturePressed("select_box_pressed_dark", darkTheme = true)

    /** Press를 주입하고 눌림 전환이 끝난 뒤 한 번만 찍는다. 시계를 멈추고 직접 흘린다. */
    private fun capturePressed(
        name: String,
        darkTheme: Boolean,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Themed(darkTheme) { Cells() } }
        composeRule.runOnIdle { pressSources.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun Themed(
        darkTheme: Boolean,
        content: @Composable () -> Unit,
    ) {
        PushlogTheme(darkTheme = darkTheme) {
            Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) { content() }
        }
    }

    /** 눌림 SINGLE 미선택·선택, 눌림 MULTIPLE 미선택·선택, 눌림 비활성 선택. */
    @Composable
    private fun Cells() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Cell(0, PushlogSelectBoxType.SINGLE, selected = false, enabled = true)
            Cell(1, PushlogSelectBoxType.SINGLE, selected = true, enabled = true)
            Cell(2, PushlogSelectBoxType.MULTIPLE, selected = false, enabled = true)
            Cell(3, PushlogSelectBoxType.MULTIPLE, selected = true, enabled = true)
            Cell(4, PushlogSelectBoxType.SINGLE, selected = true, enabled = false)
        }
    }

    @Composable
    private fun Cell(
        index: Int,
        type: PushlogSelectBoxType,
        selected: Boolean,
        enabled: Boolean,
    ) {
        PushlogSelectBox(
            selected = selected,
            onClick = {},
            type = type,
            label = "눌림 ${type.name}",
            modifier = Modifier.width(280.dp),
            description = if (selected) "선택됨" else "선택 안 됨",
            enabled = enabled,
            interactionSource = pressSources[index],
        )
    }

    private companion object {
        const val CELL_COUNT = 5
    }
}
