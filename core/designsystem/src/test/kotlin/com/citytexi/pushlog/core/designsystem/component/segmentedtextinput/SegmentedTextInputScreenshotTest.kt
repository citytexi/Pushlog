package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.requestFocus
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
class SegmentedTextInputScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statesLight() = captureStates("segmented_text_input_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("segmented_text_input_states_dark", darkTheme = true)

    /** 포커스 줄은 비어 있는 둘째 칸에 포커스를 준 뒤, 테두리 전환이 끝나게 시계를 흘려 찍는다. */
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
        composeRule
            .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.EditableText) and hasAnyAncestor(hasTestTag(FOCUSED)))[1]
            .requestFocus()
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun States() {
        val modifier = Modifier.width(320.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogResidentNumberDigits.entries.forEach { digits ->
                PushlogResidentNumberInput(remember { PushlogResidentNumberState(digits) }, modifier)
                PushlogResidentNumberInput(remember { resident(digits) }, modifier)
            }
            PushlogCardNumberMaskedDigits.entries.forEach { masked ->
                PushlogCardNumberInput(remember { PushlogCardNumberState(masked) }, modifier)
                PushlogCardNumberInput(remember { card(masked) }, modifier)
            }
            PushlogCardNumberInput(remember { card(PushlogCardNumberMaskedDigits.EIGHT, filled = 1) }, modifier.testTag(FOCUSED))
            PushlogCardNumberInput(remember { card(PushlogCardNumberMaskedDigits.EIGHT) }, modifier, invalid = true)
            PushlogResidentNumberInput(remember { resident(PushlogResidentNumberDigits.SEVEN) }, modifier, enabled = false)
            PushlogResidentNumberInput(
                remember { PushlogResidentNumberState(PushlogResidentNumberDigits.SEVEN) },
                modifier,
                enabled = false,
            )
            PushlogCardNumberInput(remember { card(PushlogCardNumberMaskedDigits.EIGHT) }, modifier, readOnly = true)
        }
    }

    private fun resident(digits: PushlogResidentNumberDigits) =
        PushlogResidentNumberState(digits).also { state ->
            state.fields.setAll(
                listOf("900101", "1234567").take(state.fields.fields.size).mapIndexed {
                    i,
                    v,
                    ->
                    v.take(state.fields.lengths[i])
                },
            )
        }

    private fun card(
        masked: PushlogCardNumberMaskedDigits,
        filled: Int = 4,
    ) = PushlogCardNumberState(masked).also { state ->
        state.fields.setAll(listOf("1234", "5678", "9012", "3456").mapIndexed { i, v -> if (i < filled) v else "" })
    }

    private companion object {
        const val FOCUSED = "focused"
    }
}
