package com.citytexi.pushlog.preview.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.preview.foundation.ColorPreviewContent
import com.citytexi.pushlog.preview.foundation.MaterialPreviewContent
import com.citytexi.pushlog.preview.foundation.ShapePreviewContent
import com.citytexi.pushlog.preview.foundation.TypographyPreviewContent
import com.citytexi.pushlog.preview.foundation.allTypographyStyles
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** foundation 토큰을 눈으로 확인하는 카탈로그 캡처. 기준 이미지는 커밋하지 않는다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w412dp-h6000dp")
class FoundationCatalogScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun colorLight() = capture("catalog_color_light", darkTheme = false) { ColorPreviewContent() }

    @Test
    fun colorDark() = capture("catalog_color_dark", darkTheme = true) { ColorPreviewContent() }

    @Test
    fun typographyLight() = capture("catalog_typography_light", darkTheme = false) { TypographyPreviewContent(allTypographyStyles()) }

    @Test
    fun typographyDark() = capture("catalog_typography_dark", darkTheme = true) { TypographyPreviewContent(allTypographyStyles()) }

    @Test
    fun typographyFontScale2Light() =
        capture("catalog_typography_fontscale2_light", darkTheme = false) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                TypographyPreviewContent(allTypographyStyles().filter { (name, _) -> name.matches(REGULAR_STYLE) })
            }
        }

    @Test
    fun shapeLight() = capture("catalog_shape_light", darkTheme = false) { ShapePreviewContent() }

    @Test
    fun shapeDark() = capture("catalog_shape_dark", darkTheme = true) { ShapePreviewContent() }

    @Test
    fun materialLight() = capture("catalog_material_light", darkTheme = false) { MaterialPreviewContent() }

    @Test
    fun materialDark() = capture("catalog_material_dark", darkTheme = true) { MaterialPreviewContent() }

    private fun capture(
        name: String,
        darkTheme: Boolean,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                Box(Modifier.width(412.dp).background(PushlogTheme.color.bg.layerDefault)) {
                    content()
                }
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }

    private companion object {
        val REGULAR_STYLE = Regex("t[0-9]+Regular")
    }
}
