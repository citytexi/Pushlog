package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
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
class PushlogSwitchmarkScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val rows =
        PushlogSwitchmarkTone.entries.flatMap { tone -> PushlogSwitchmarkSize.entries.map { size -> tone to size } }

    private val states = listOf(false, true)

    private val pressSources =
        rows
            .flatMap { row -> states.map { checked -> row to checked } }
            .associateWith { MutableInteractionSource() }

    @Test
    fun pressedLight() = capturePressed("switchmark_pressed_light", darkTheme = false)

    @Test
    fun pressedDark() = capturePressed("switchmark_pressed_dark", darkTheme = true)

    /**
     * tone × size마다 한 줄, 칸은 눌린 꺼짐·켜짐이다.
     * 눌림 전환이 끝난 뒤 한 번만 찍으려고 시계를 멈추고 직접 흘린다.
     */
    private fun capturePressed(
        name: String,
        darkTheme: Boolean,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                Column(
                    Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rows.forEach { (tone, size) ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            states.forEach { checked ->
                                PushlogSwitchmark(
                                    checked = checked,
                                    tone = tone,
                                    size = size,
                                    interactionSource = pressSources.getValue((tone to size) to checked),
                                )
                            }
                        }
                    }
                }
            }
        }
        composeRule.runOnIdle { pressSources.values.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }
}
