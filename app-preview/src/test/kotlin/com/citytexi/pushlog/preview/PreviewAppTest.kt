package com.citytexi.pushlog.preview

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PreviewAppTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `choosing dark in the control bar paints the home with the dark layerDefault`() {
        var light = Color.Unspecified
        var dark = Color.Unspecified
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                val color = PushlogTheme.color.bg.layerDefault
                SideEffect { light = color }
            }
            PushlogTheme(darkTheme = true) {
                val color = PushlogTheme.color.bg.layerDefault
                SideEffect { dark = color }
            }
            PreviewApp()
        }
        composeRule.onNodeWithText("라이트").performClick()
        assertEquals(light, homeBackground())

        composeRule.onNodeWithText("다크").performClick()
        assertEquals(dark, homeBackground())
    }

    @Test
    fun `control bar and home at font scale 2`() {
        composeRule.setContent { PreviewApp() }
        composeRule.onNodeWithText("라이트").performClick()
        composeRule.onNodeWithText("2×").performClick()
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/preview_app_fontscale2_light.png")
    }

    @Test
    fun `scaffold title at font scale 2`() {
        composeRule.setContent {
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 2f)) {
                PushlogTheme(darkTheme = false) {
                    PreviewScaffold(title = "LoadingContent long title check", onBack = {}) {
                        Text("Body")
                    }
                }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/preview_scaffold_fontscale2_light.png")
    }

    // The home draws no own surface above PreviewApp's root, so the top-left gutter pixel is its background.
    private fun homeBackground(): Color {
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()[1, 1]
    }
}
