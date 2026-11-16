package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
class PushlogActionButtonScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSources = PushlogActionButtonVariant.entries.associateWith { MutableInteractionSource() }

    @Test
    fun statesLight() = captureStates("action_button_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("action_button_states_dark", darkTheme = true)

    /**
     * 눌림 열은 Press를 주입한 뒤 한 번만 찍는다. 같은 파일을 두 번 쓰면 기본 상태가 덮인다.
     * 로딩 호는 무한 전환이라 시계를 멈추고 직접 흘려야 보인다. 화면을 넓혀 열이 잘리지 않게 한다.
     */
    private fun captureStates(
        name: String,
        darkTheme: Boolean,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Themed(darkTheme) { States() } }
        composeRule.runOnIdle { pressSources.values.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
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

    @Composable
    private fun States() {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogActionButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogActionButton(text = "확인", onClick = {}, variant = variant)
                    PushlogActionButton(text = "눌림", onClick = {}, variant = variant, interactionSource = pressSources.getValue(variant))
                    PushlogActionButton(text = "비활성", onClick = {}, variant = variant, enabled = false)
                    PushlogActionButton(text = "로딩", onClick = {}, variant = variant, loading = true)
                }
            }
        }
    }
}
