package com.citytexi.pushlog.preview

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.preview.foundation.BRAND_SOLID_SWATCH_TAG
import com.citytexi.pushlog.preview.scaffold.PREVIEW_SCAFFOLD_TAG
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PreviewAppTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

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
    fun `choosing dark in the control bar paints a detail screen with the dark layerDefault`() {
        var light = Color.Unspecified
        var dark = Color.Unspecified
        var lightBrand = Color.Unspecified
        var darkBrand = Color.Unspecified
        composeRule.setContent {
            PushlogTheme(darkTheme = false) {
                val color = PushlogTheme.color.bg.layerDefault
                SideEffect { light = color }
                val brand = PushlogTheme.color.bg.brandSolid
                SideEffect { lightBrand = brand }
            }
            PushlogTheme(darkTheme = true) {
                val color = PushlogTheme.color.bg.layerDefault
                SideEffect { dark = color }
                val brand = PushlogTheme.color.bg.brandSolid
                SideEffect { darkBrand = brand }
            }
            PreviewApp()
        }
        composeRule.onNodeWithText("라이트").performClick()
        composeRule.onNodeWithText("Color").performClick()
        composeRule.onNodeWithText("뒤로").assertExists()
        assertEquals(light, detailBackground())
        val lightSwatch = brandSolidSwatch()
        assertEquals(lightBrand, lightSwatch)

        composeRule.onNodeWithText("다크").performClick()
        composeRule.onNodeWithText("뒤로").assertExists()
        assertEquals(dark, detailBackground())
        val darkSwatch = brandSolidSwatch()
        assertEquals(darkBrand, darkSwatch)
        assertNotEquals(lightSwatch, darkSwatch)
    }

    @Test
    fun `tapping back twice quickly on a detail screen returns home without finishing the app`() {
        composeRule.setContent { PreviewApp() }
        composeRule.onNodeWithText("Color").performClick()
        // Both taps hit the same back button before any recomposition, like two taps inside one pop transition.
        val tapBack =
            composeRule
                .onNodeWithText("뒤로")
                .fetchSemanticsNode()
                .config[SemanticsActions.OnClick]
                .action
        composeRule.runOnUiThread {
            tapBack?.invoke()
            tapBack?.invoke()
        }
        composeRule.waitForIdle()
        assertFalse(composeRule.activity.isFinishing)
        composeRule.onNodeWithText("Color").assertExists()
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

    // The tagged scaffold is the detail screen's own root, so its top-left pixel is what the detail shows.
    private fun detailBackground(): Color {
        composeRule.waitForIdle()
        return composeRule.onNodeWithTag(PREVIEW_SCAFFOLD_TAG).captureToImage().toPixelMap()[1, 1]
    }

    // The swatch box is drawn by the Color screen itself, so its center pixel shows the theme the detail content got.
    private fun brandSolidSwatch(): Color {
        composeRule.waitForIdle()
        val image = composeRule.onNodeWithTag(BRAND_SOLID_SWATCH_TAG).captureToImage().toPixelMap()
        return image[image.width / 2, image.height / 2]
    }

    // Home and detail screens draw no own surface above PreviewApp's root, so the top-left pixel is its background.
    private fun homeBackground(): Color {
        composeRule.waitForIdle()
        return composeRule.onRoot().captureToImage().toPixelMap()[1, 1]
    }
}
