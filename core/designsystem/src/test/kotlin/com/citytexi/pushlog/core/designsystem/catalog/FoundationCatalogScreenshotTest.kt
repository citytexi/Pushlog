package com.citytexi.pushlog.core.designsystem.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.shadow.PushlogShadowToken
import com.citytexi.pushlog.core.designsystem.theme.shadow.pushlogShadow
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private const val SAMPLE_TEXT = "알림 보관함 Aa 123"

/** foundation 토큰을 눈으로 확인하는 카탈로그 캡처. 기준 이미지는 커밋하지 않는다. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w412dp-h6000dp")
class FoundationCatalogScreenshotTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun colorLight() = capture("catalog_color_light", darkTheme = false) { ColorCatalog() }

    @Test
    fun colorDark() = capture("catalog_color_dark", darkTheme = true) { ColorCatalog() }

    @Test
    fun typographyLight() = capture("catalog_typography_light", darkTheme = false) { TypographyCatalog(allTypography()) }

    @Test
    fun typographyDark() = capture("catalog_typography_dark", darkTheme = true) { TypographyCatalog(allTypography()) }

    @Test
    fun typographyFontScale2Light() =
        capture("catalog_typography_fontscale2_light", darkTheme = false) {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 2f)) {
                TypographyCatalog(regularTypography())
            }
        }

    @Test
    fun shapeLight() = capture("catalog_shape_light", darkTheme = false) { ShapeCatalog() }

    @Test
    fun shapeDark() = capture("catalog_shape_dark", darkTheme = true) { ShapeCatalog() }

    @Test
    fun materialLight() = capture("catalog_material_light", darkTheme = false) { MaterialCatalog() }

    @Test
    fun materialDark() = capture("catalog_material_dark", darkTheme = true) { MaterialCatalog() }

    private fun capture(
        name: String,
        darkTheme: Boolean,
        content: @Composable () -> Unit,
    ) {
        composeRule.setContent {
            PushlogTheme(darkTheme = darkTheme) {
                CatalogRoot(content)
            }
        }
        composeRule.onRoot().captureRoboImage("build/outputs/roborazzi/$name.png")
    }
}

@Composable
private fun CatalogRoot(content: @Composable () -> Unit) {
    Column(
        modifier =
            Modifier
                .width(412.dp)
                .background(PushlogTheme.color.bg.layerDefault)
                .padding(PushlogTheme.dimension.x4),
        verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
    ) {
        content()
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = PushlogTheme.typography.t5Bold,
        color = PushlogTheme.color.fg.neutral,
        modifier = Modifier.padding(top = PushlogTheme.dimension.x3),
    )
}

@Composable
private fun ColorCatalog() {
    SectionTitle("bg")
    bgSwatches().forEach { (name, color) -> Swatch(name, color) }
    SectionTitle("fg")
    fgSwatches().forEach { (name, color) -> Swatch(name, color) }
    SectionTitle("stroke")
    strokeSwatches().forEach { (name, color) -> Swatch(name, color) }
}

@Composable
private fun Swatch(
    name: String,
    color: Color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .size(width = PushlogTheme.dimension.x12, height = PushlogTheme.dimension.x6)
                .background(color)
                .border(1.dp, PushlogTheme.color.stroke.neutralMuted),
        )
        Text(name, style = PushlogTheme.typography.t3Regular, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun TypographyCatalog(styles: List<Pair<String, TextStyle>>) {
    styles.forEach { (name, style) ->
        Text(name, style = PushlogTheme.typography.t2Regular, color = PushlogTheme.color.fg.neutralMuted)
        Text(SAMPLE_TEXT, style = style, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun ShapeCatalog() {
    val d = PushlogTheme.dimension
    SectionTitle("spacing")
    listOf(
        "x0Half" to d.x0Half,
        "x1" to d.x1,
        "x1Half" to d.x1Half,
        "x2" to d.x2,
        "x2Half" to d.x2Half,
        "x3" to d.x3,
        "x3Half" to d.x3Half,
        "x4" to d.x4,
        "x4Half" to d.x4Half,
        "x5" to d.x5,
        "x6" to d.x6,
        "x7" to d.x7,
        "x8" to d.x8,
        "x9" to d.x9,
        "x10" to d.x10,
        "x12" to d.x12,
        "x13" to d.x13,
        "x14" to d.x14,
        "x16" to d.x16,
        "spacingX.globalGutter" to d.spacingX.globalGutter,
        "spacingX.betweenChips" to d.spacingX.betweenChips,
        "spacingY.betweenText" to d.spacingY.betweenText,
        "spacingY.componentDefault" to d.spacingY.componentDefault,
        "spacingY.navToTitle" to d.spacingY.navToTitle,
        "spacingY.screenBottom" to d.spacingY.screenBottom,
    ).forEach { (name, size) -> SpacingBar(name, size) }

    SectionTitle("radius")
    val r = PushlogTheme.radius
    listOf(
        "r0Half" to RoundedCornerShape(r.r0Half),
        "r1" to RoundedCornerShape(r.r1),
        "r1Half" to RoundedCornerShape(r.r1Half),
        "r2" to RoundedCornerShape(r.r2),
        "r2Half" to RoundedCornerShape(r.r2Half),
        "r3" to RoundedCornerShape(r.r3),
        "r3Half" to RoundedCornerShape(r.r3Half),
        "r4" to RoundedCornerShape(r.r4),
        "r5" to RoundedCornerShape(r.r5),
        "r6" to RoundedCornerShape(r.r6),
        "full" to r.full,
    ).forEach { (name, shape) -> RadiusBox(name, shape) }

    SectionTitle("shadow")
    val s = PushlogTheme.shadow
    listOf("s1" to s.s1, "s2" to s.s2, "s3" to s.s3).forEach { (name, token) -> ShadowCard(name, token) }
}

@Composable
private fun SpacingBar(
    name: String,
    size: Dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .width(size)
                .height(PushlogTheme.dimension.x3)
                .background(PushlogTheme.color.bg.brandSolid),
        )
        Text("$name ${size.value.toInt()}dp", style = PushlogTheme.typography.t2Regular, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun RadiusBox(
    name: String,
    shape: Shape,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .size(PushlogTheme.dimension.x12)
                .background(PushlogTheme.color.bg.brandWeak, shape)
                .border(1.dp, PushlogTheme.color.stroke.brandSolid, shape),
        )
        Text(name, style = PushlogTheme.typography.t3Regular, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun ShadowCard(
    name: String,
    token: PushlogShadowToken,
) {
    val shape = RoundedCornerShape(PushlogTheme.radius.r3)
    Box(
        Modifier
            .padding(PushlogTheme.dimension.x3)
            .fillMaxWidth()
            .height(PushlogTheme.dimension.x16)
            .pushlogShadow(token, shape)
            .background(PushlogTheme.color.bg.layerFloating, shape)
            .padding(PushlogTheme.dimension.x4),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(name, style = PushlogTheme.typography.t4Medium, color = PushlogTheme.color.fg.neutral)
    }
}

@Composable
private fun MaterialCatalog() {
    Button(onClick = {}) { Text("Button") }
    FilledTonalButton(onClick = {}) { Text("FilledTonalButton") }
    OutlinedButton(onClick = {}) { Text("OutlinedButton") }
    Card(modifier = Modifier.fillMaxWidth()) {
        Text("Card", modifier = Modifier.padding(PushlogTheme.dimension.x4))
    }
    Snackbar(action = { TextButton(onClick = {}) { Text("Action") } }) { Text("Snackbar") }
    OutlinedTextField(value = "OutlinedTextField", onValueChange = {})
}

@Composable
private fun bgSwatches(): List<Pair<String, Color>> {
    val c = PushlogTheme.color
    return listOf(
        "brandSolid" to c.bg.brandSolid,
        "brandSolidPressed" to c.bg.brandSolidPressed,
        "brandWeak" to c.bg.brandWeak,
        "brandWeakPressed" to c.bg.brandWeakPressed,
        "criticalSolid" to c.bg.criticalSolid,
        "criticalSolidPressed" to c.bg.criticalSolidPressed,
        "criticalWeak" to c.bg.criticalWeak,
        "criticalWeakPressed" to c.bg.criticalWeakPressed,
        "disabled" to c.bg.disabled,
        "informativeSolid" to c.bg.informativeSolid,
        "informativeSolidPressed" to c.bg.informativeSolidPressed,
        "informativeWeak" to c.bg.informativeWeak,
        "informativeWeakPressed" to c.bg.informativeWeakPressed,
        "layerBasement" to c.bg.layerBasement,
        "layerDefault" to c.bg.layerDefault,
        "layerDefaultPressed" to c.bg.layerDefaultPressed,
        "layerFloating" to c.bg.layerFloating,
        "layerFloatingPressed" to c.bg.layerFloatingPressed,
        "magicWeak" to c.bg.magicWeak,
        "neutralInverted" to c.bg.neutralInverted,
        "neutralInvertedPressed" to c.bg.neutralInvertedPressed,
        "neutralMuted" to c.bg.neutralMuted,
        "neutralSolid" to c.bg.neutralSolid,
        "neutralSolidPressed" to c.bg.neutralSolidPressed,
        "neutralSolidMuted" to c.bg.neutralSolidMuted,
        "neutralSolidMutedPressed" to c.bg.neutralSolidMutedPressed,
        "neutralSubtle" to c.bg.neutralSubtle,
        "neutralWeak" to c.bg.neutralWeak,
        "neutralWeakAlpha" to c.bg.neutralWeakAlpha,
        "neutralWeakAlphaPressed" to c.bg.neutralWeakAlphaPressed,
        "neutralWeakPressed" to c.bg.neutralWeakPressed,
        "overlay" to c.bg.overlay,
        "overlayMuted" to c.bg.overlayMuted,
        "positiveSolid" to c.bg.positiveSolid,
        "positiveSolidPressed" to c.bg.positiveSolidPressed,
        "positiveWeak" to c.bg.positiveWeak,
        "positiveWeakPressed" to c.bg.positiveWeakPressed,
        "transparent" to c.bg.transparent,
        "transparentPressed" to c.bg.transparentPressed,
        "transparentSelected" to c.bg.transparentSelected,
        "transparentSelectedPressed" to c.bg.transparentSelectedPressed,
        "warningSolid" to c.bg.warningSolid,
        "warningSolidPressed" to c.bg.warningSolidPressed,
        "warningWeak" to c.bg.warningWeak,
        "warningWeakPressed" to c.bg.warningWeakPressed,
    )
}

@Composable
private fun fgSwatches(): List<Pair<String, Color>> {
    val c = PushlogTheme.color
    return listOf(
        "brand" to c.fg.brand,
        "brandContrast" to c.fg.brandContrast,
        "critical" to c.fg.critical,
        "criticalContrast" to c.fg.criticalContrast,
        "disabled" to c.fg.disabled,
        "informative" to c.fg.informative,
        "informativeContrast" to c.fg.informativeContrast,
        "neutral" to c.fg.neutral,
        "neutralInverted" to c.fg.neutralInverted,
        "neutralMuted" to c.fg.neutralMuted,
        "neutralSubtle" to c.fg.neutralSubtle,
        "onBrandSolid" to c.fg.onBrandSolid,
        "onCriticalSolid" to c.fg.onCriticalSolid,
        "onInformativeSolid" to c.fg.onInformativeSolid,
        "onNeutralSolid" to c.fg.onNeutralSolid,
        "onPositiveSolid" to c.fg.onPositiveSolid,
        "onWarningSolid" to c.fg.onWarningSolid,
        "placeholder" to c.fg.placeholder,
        "positive" to c.fg.positive,
        "positiveContrast" to c.fg.positiveContrast,
        "warning" to c.fg.warning,
        "warningContrast" to c.fg.warningContrast,
    )
}

@Composable
private fun strokeSwatches(): List<Pair<String, Color>> {
    val c = PushlogTheme.color
    return listOf(
        "brandSolid" to c.stroke.brandSolid,
        "brandWeak" to c.stroke.brandWeak,
        "criticalSolid" to c.stroke.criticalSolid,
        "criticalWeak" to c.stroke.criticalWeak,
        "focusRing" to c.stroke.focusRing,
        "informativeSolid" to c.stroke.informativeSolid,
        "informativeWeak" to c.stroke.informativeWeak,
        "neutralContrast" to c.stroke.neutralContrast,
        "neutralMuted" to c.stroke.neutralMuted,
        "neutralSolid" to c.stroke.neutralSolid,
        "neutralSubtle" to c.stroke.neutralSubtle,
        "neutralWeak" to c.stroke.neutralWeak,
        "positiveSolid" to c.stroke.positiveSolid,
        "positiveWeak" to c.stroke.positiveWeak,
        "warningSolid" to c.stroke.warningSolid,
        "warningWeak" to c.stroke.warningWeak,
    )
}

@Composable
private fun allTypography(): List<Pair<String, TextStyle>> {
    val t = PushlogTheme.typography
    return listOf(
        "screenTitle" to t.screenTitle,
        "articleBody" to t.articleBody,
        "articleNote" to t.articleNote,
        "t1Regular" to t.t1Regular,
        "t1Medium" to t.t1Medium,
        "t1Bold" to t.t1Bold,
        "t2Regular" to t.t2Regular,
        "t2Medium" to t.t2Medium,
        "t2Bold" to t.t2Bold,
        "t3Regular" to t.t3Regular,
        "t3Medium" to t.t3Medium,
        "t3Bold" to t.t3Bold,
        "t4Regular" to t.t4Regular,
        "t4Medium" to t.t4Medium,
        "t4Bold" to t.t4Bold,
        "t5Regular" to t.t5Regular,
        "t5Medium" to t.t5Medium,
        "t5Bold" to t.t5Bold,
        "t6Regular" to t.t6Regular,
        "t6Medium" to t.t6Medium,
        "t6Bold" to t.t6Bold,
        "t7Regular" to t.t7Regular,
        "t7Medium" to t.t7Medium,
        "t7Bold" to t.t7Bold,
        "t8Regular" to t.t8Regular,
        "t8Medium" to t.t8Medium,
        "t8Bold" to t.t8Bold,
        "t9Regular" to t.t9Regular,
        "t9Medium" to t.t9Medium,
        "t9Bold" to t.t9Bold,
        "t10Regular" to t.t10Regular,
        "t10Medium" to t.t10Medium,
        "t10Bold" to t.t10Bold,
        "t11Regular" to t.t11Regular,
        "t11Medium" to t.t11Medium,
        "t11Bold" to t.t11Bold,
        "t12Regular" to t.t12Regular,
        "t12Medium" to t.t12Medium,
        "t12Bold" to t.t12Bold,
        "t13Regular" to t.t13Regular,
        "t13Medium" to t.t13Medium,
        "t13Bold" to t.t13Bold,
        "t14Regular" to t.t14Regular,
        "t14Medium" to t.t14Medium,
        "t14Bold" to t.t14Bold,
    )
}

@Composable
private fun regularTypography(): List<Pair<String, TextStyle>> =
    allTypography().filter { (name, _) -> name.matches(Regex("t[0-9]+Regular")) }
