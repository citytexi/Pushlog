package com.citytexi.pushlog.core.designsystem.component.searchinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.down
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogSearchInputScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun statesLight() = captureStates("search_input_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("search_input_states_dark", darkTheme = true)

    /**
     * 지우기 버튼은 interactionSource를 밖에 열지 않아 Press를 주입할 수 없다.
     * 손가락을 대고 떼지 않은 채 시계를 흘려 눌림 전환이 끝난 뒤 찍는다.
     */
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
        composeRule.onNodeWithContentDescription(PRESS_LABEL).performTouchInput { down(center) }
        composeRule.mainClock.advanceTimeBy(800)
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun States() {
        val modifier = Modifier.width(280.dp)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogSearchInput(remember { TextFieldState() }, onSubmit = {}, modifier = modifier, placeholder = "검색")
            PushlogSearchInput(remember { TextFieldState("커피") }, onSubmit = {}, modifier = modifier, clearButtonLabel = PRESS_LABEL)
            PushlogSearchInput(remember { TextFieldState("커피") }, onSubmit = {}, modifier = modifier, clearButtonLabel = "지우기")
            PushlogSearchInput(remember { TextFieldState() }, onSubmit = {}, modifier = modifier, placeholder = "검색", enabled = false)
            PushlogSearchInput(remember { TextFieldState("커피") }, onSubmit = {}, modifier = modifier, enabled = false)
        }
    }

    private companion object {
        const val PRESS_LABEL = "눌림 지우기"
    }
}
