package com.citytexi.pushlog.core.designsystem.component.inputbutton

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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
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
class PushlogInputButtonScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSource = MutableInteractionSource()

    @Test
    fun statesLight() = captureStates("input_button_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("input_button_states_dark", darkTheme = true)

    /** 눌림 줄은 Press를 주입하고 시계를 흘려 전환이 끝난 뒤 한 번만 찍는다. 오류 테두리 전환도 이때 끝난다. */
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
        composeRule.runOnIdle { pressSource.tryEmit(PressInteraction.Press(Offset.Zero)) }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun States() {
        val modifier = Modifier.width(280.dp)
        val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
        val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogInputButton(null, onClick = {}, modifier = modifier, placeholder = "날짜 선택", contentDescription = NAME)
            PushlogInputButton("1990.01.01", onClick = {}, modifier = modifier, contentDescription = NAME)
            PushlogInputButton("1990.01.01", onClick = {}, modifier = modifier, onClear = {}, contentDescription = NAME)
            PushlogInputButton(
                "10000",
                onClick = {},
                modifier = modifier,
                prefixIcon = plus,
                prefixText = "₩",
                suffixText = "원",
                suffixIcon = down,
                contentDescription = NAME,
            )
            PushlogInputButton("오류", onClick = {}, modifier = modifier, contentDescription = NAME, invalid = true)
            PushlogInputButton("읽기 전용", onClick = {}, modifier = modifier, onClear = {}, contentDescription = NAME, readOnly = true)
            PushlogInputButton("비활성", onClick = {}, modifier = modifier, suffixIcon = down, contentDescription = NAME, enabled = false)
            PushlogInputButton("눌림", onClick = {}, modifier = modifier, contentDescription = NAME, interactionSource = pressSource)
        }
    }

    private companion object {
        const val NAME = "생일"
    }
}
