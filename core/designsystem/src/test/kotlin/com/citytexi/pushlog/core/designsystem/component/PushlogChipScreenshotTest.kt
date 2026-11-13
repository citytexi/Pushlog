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
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w1200dp-h1200dp")
class PushlogChipScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val pressSources =
        PushlogChipVariant.entries
            .flatMap { variant -> listOf(false, true).map { selected -> variant to selected } }
            .associateWith { MutableInteractionSource() }

    @Test
    fun statesLight() = captureStates("chip_states_light", darkTheme = false)

    @Test
    fun statesDark() = captureStates("chip_states_dark", darkTheme = true)

    @Test
    fun sizesLight() = capture("chip_sizes_light", darkTheme = false) { Sizes() }

    @Test
    fun sizesDark() = capture("chip_sizes_dark", darkTheme = true) { Sizes() }

    @Test
    fun font2xLight() = captureStates("chip_font2x_light", darkTheme = false, fontScale = 2f)

    @Test
    fun font2xDark() = captureStates("chip_font2x_dark", darkTheme = true, fontScale = 2f)

    /**
     * 눌림 열은 Press를 주입한 뒤 한 번만 찍는다. 같은 파일을 두 번 쓰면 기본 상태가 덮인다.
     * 로딩 호는 무한 전환이라 시계를 멈추고 직접 흘려야 보인다. 화면을 넓혀 2배 글자에서도 열이 잘리지 않게 한다.
     */
    private fun captureStates(
        name: String,
        darkTheme: Boolean,
        fontScale: Float = 1f,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent { Themed(darkTheme, fontScale) { States() } }
        composeRule.runOnIdle { pressSources.values.forEach { it.tryEmit(PressInteraction.Press(Offset.Zero)) } }
        composeRule.mainClock.advanceTimeBy(800)
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
                Column(Modifier.background(PushlogTheme.color.bg.layerDefault).padding(8.dp)) { content() }
            }
        }
    }

    /** variant마다 세 줄: 기본·눌림 / selected·selected+눌림 / 비활성·selected+비활성. 눌림 열은 Press를 주입해 찍는다. */
    @Composable
    private fun States() {
        val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
        val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogChipVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogChip(text = "기본", onClick = {}, variant = variant, prefixIcon = plus, suffixIcon = down)
                    PushlogChip(
                        text = "눌림",
                        onClick = {},
                        variant = variant,
                        interactionSource = pressSources.getValue(variant to false),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogChip(text = "선택", onClick = {}, variant = variant, selected = true, prefixIcon = plus)
                    PushlogChip(
                        text = "선택눌림",
                        onClick = {},
                        variant = variant,
                        selected = true,
                        interactionSource = pressSources.getValue(variant to true),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogChip(text = "비활성", onClick = {}, variant = variant, enabled = false)
                    PushlogChip(text = "선택비활성", onClick = {}, variant = variant, selected = true, enabled = false)
                }
            }
        }
    }

    @Composable
    private fun Sizes() {
        val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
        val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PushlogChipSize.entries.forEach { size ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PushlogChip(text = "글자", onClick = {}, size = size)
                    PushlogChip(text = "앞 아이콘", onClick = {}, size = size, prefixIcon = plus)
                    PushlogChip(text = "뒤 아이콘", onClick = {}, size = size, suffixIcon = down)
                }
            }
        }
    }
}
