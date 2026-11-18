package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
class PushlogTextInputScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val focusSources =
        PushlogTextInputVariant.entries
            .flatMap { variant -> listOf(false, true).map { invalid -> variant to invalid } }
            .associateWith { MutableInteractionSource() }

    @Test
    fun statesLight() = captureStates("text_input_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("text_input_states_dark", darkTheme = true)

    @Test
    fun textAreaLight() = captureTextAreas("text_area_light", darkTheme = false)

    @Test
    fun textAreaDark() = captureTextAreas("text_area_dark", darkTheme = true)

    private fun captureTextAreas(
        name: String,
        darkTheme: Boolean,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) { TextAreas() }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun TextAreas() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogTextInputVariant.entries.forEach { variant ->
                val modifier = Modifier.width(280.dp)
                PushlogTextArea(remember { TextFieldState() }, modifier, variant, placeholder = "기본")
                PushlogTextArea(remember { TextFieldState("첫째 줄\n둘째 줄\n셋째 줄\n넷째 줄\n다섯째 줄") }, modifier, variant)
                PushlogTextArea(remember { TextFieldState() }, modifier, variant, invalid = true, placeholder = "오류")
                PushlogTextArea(remember { TextFieldState("읽기") }, modifier, variant, readOnly = true)
                PushlogTextArea(remember { TextFieldState() }, modifier, variant, enabled = false, placeholder = "비활성")
                PushlogTextArea(remember { TextFieldState("값") }, modifier, variant, prefixText = "접두", suffixText = "접미")
            }
        }
    }

    /** 포커스 줄은 Focus를 직접 주입한다. 줄마다 포커스가 필요해 실제 포커스 하나로는 여러 줄을 못 찍는다. 시계를 흘려 전환이 끝난 뒤 찍는다. */
    private fun captureStates(
        name: String,
        darkTheme: Boolean,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) { States() }
            }
        }
        composeRule.runOnIdle { focusSources.values.forEach { it.tryEmit(FocusInteraction.Focus()) } }
        composeRule.mainClock.advanceTimeBy(200)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun States() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogTextInputVariant.entries.forEach { variant ->
                val modifier = Modifier.width(280.dp)
                PushlogTextInput(remember { TextFieldState() }, modifier, variant, placeholder = "기본")
                PushlogTextInput(remember { TextFieldState("값") }, modifier, variant, placeholder = "값")
                PushlogTextInput(remember { TextFieldState() }, modifier, variant, invalid = true, placeholder = "오류")
                PushlogTextInput(remember { TextFieldState("읽기") }, modifier, variant, readOnly = true)
                PushlogTextInput(remember { TextFieldState() }, modifier, variant, enabled = false, placeholder = "비활성")
                PushlogTextInput(
                    remember { TextFieldState("포커스") },
                    modifier,
                    variant,
                    prefixText = "접두",
                    suffixText = "접미",
                    interactionSource = focusSources.getValue(variant to false),
                )
                PushlogTextInput(
                    remember { TextFieldState("오류 포커스") },
                    modifier,
                    variant,
                    invalid = true,
                    interactionSource = focusSources.getValue(variant to true),
                )
            }
        }
    }
}
