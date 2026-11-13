package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PushlogActionButtonScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSources = PushlogActionButtonVariant.entries.associateWith { MutableInteractionSource() }

    @Test
    fun statesLight() = captureStates("action_button_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("action_button_states_dark", darkTheme = true)

    @Test
    fun sizesLight() = capture("action_button_sizes_light", darkTheme = false) { Sizes() }

    @Test
    fun sizesDark() = capture("action_button_sizes_dark", darkTheme = true) { Sizes() }

    @Test
    fun font2xLight() = captureStates("action_button_font2x_light", darkTheme = false, fontScale = 2f)

    @Test
    fun font2xDark() = captureStates("action_button_font2x_dark", darkTheme = true, fontScale = 2f)

    private fun captureStates(
        name: String,
        darkTheme: Boolean,
        fontScale: Float = 1f,
    ) {
        capture(name, darkTheme, fontScale) { States() }
        composeRule.runOnIdle { pressSources.values.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun capture(
        name: String,
        darkTheme: Boolean,
        fontScale: Float = 1f,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                PushlogTheme(darkTheme = darkTheme) {
                    Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) { content() }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    /** 행마다 variant, 열은 기본·눌림·비활성·로딩. 눌림 열은 캡처 뒤 Press를 주입해 다시 찍는다. */
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

    @Composable
    private fun Sizes() {
        val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogActionButtonSize.entries.forEach { size ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogActionButton(text = "확인", onClick = {}, size = size)
                    PushlogActionButton(text = "확인", onClick = {}, size = size, prefixIcon = plus)
                    PushlogActionButton(icon = plus, contentDescription = "추가", onClick = {}, size = size)
                }
            }
        }
    }
}
