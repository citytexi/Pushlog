package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
class PushlogRadiomarkScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val states = listOf(false, true)

    private val pressSources =
        PushlogRadiomarkTone.entries
            .flatMap { tone -> states.map { selected -> tone to selected } }
            .associateWith { MutableInteractionSource() }

    @Test
    fun pressedLight() = capturePressed("radiomark_pressed_light", darkTheme = false)

    @Test
    fun pressedDark() = capturePressed("radiomark_pressed_dark", darkTheme = true)

    /**
     * tone마다 한 줄, 칸은 눌린 꺼짐·선택이다.
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
                    PushlogRadiomarkTone.entries.forEach { tone ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            states.forEach { selected ->
                                PushlogRadiomark(
                                    selected = selected,
                                    tone = tone,
                                    interactionSource = pressSources.getValue(tone to selected),
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
