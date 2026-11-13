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
class PushlogContextualFloatingButtonScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSources = PushlogContextualFloatingButtonVariant.entries.associateWith { MutableInteractionSource() }

    @Test
    fun statesLight() = captureStates("contextual_floating_button_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("contextual_floating_button_states_dark", darkTheme = true)

    @Test
    fun sizesLight() = capture("contextual_floating_button_sizes_light", darkTheme = false) { Sizes() }

    @Test
    fun sizesDark() = capture("contextual_floating_button_sizes_dark", darkTheme = true) { Sizes() }

    @Test
    fun font2xLight() = captureStates("contextual_floating_button_font2x_light", darkTheme = false, fontScale = 2f)

    @Test
    fun font2xDark() = captureStates("contextual_floating_button_font2x_dark", darkTheme = true, fontScale = 2f)

    /** 눌림 열은 Press를 주입한 뒤 한 번만 찍는다. 같은 파일을 두 번 쓰면 기본 상태가 덮인다. */
    private fun captureStates(
        name: String,
        darkTheme: Boolean,
        fontScale: Float = 1f,
    ) {
        composeRule.setContent { Themed(darkTheme, fontScale) { States() } }
        composeRule.runOnIdle { pressSources.values.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
        composeRule.mainClock.advanceTimeBy(300)
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private fun capture(
        name: String,
        darkTheme: Boolean,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent { Themed(darkTheme, 1f, content) }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    @Composable
    private fun Themed(
        darkTheme: Boolean,
        fontScale: Float,
        content: @Composable () -> Unit,
    ) {
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
            PushlogTheme(darkTheme = darkTheme) {
                Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(16.dp)) { content() }
            }
        }
    }

    /** 행마다 variant, 열은 기본·눌림·비활성·로딩. */
    @Composable
    private fun States() {
        val arrow = rememberVectorPainter(PreviewSampleIcons.ArrowUp)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PushlogContextualFloatingButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PushlogContextualFloatingButton(text = "맨 위로", onClick = {}, variant = variant, prefixIcon = arrow)
                    PushlogContextualFloatingButton(
                        text = "눌림",
                        onClick = {},
                        variant = variant,
                        interactionSource = pressSources.getValue(variant),
                    )
                    PushlogContextualFloatingButton(text = "비활성", onClick = {}, variant = variant, enabled = false)
                    PushlogContextualFloatingButton(text = "로딩", onClick = {}, variant = variant, loading = true)
                }
            }
        }
    }

    @Composable
    private fun Sizes() {
        val arrow = rememberVectorPainter(PreviewSampleIcons.ArrowUp)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PushlogContextualFloatingButton(text = "맨 위로", onClick = {})
            PushlogContextualFloatingButton(text = "맨 위로", onClick = {}, prefixIcon = arrow)
            PushlogContextualFloatingButton(icon = arrow, contentDescription = "맨 위로", onClick = {})
        }
    }
}
