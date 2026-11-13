package com.citytexi.pushlog.preview.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val SWATCH_BORDER_WIDTH = 1.dp

/** Test tag on the bg.brandSolid swatch, so tests can read the color the screen itself draws. */
const val BRAND_SOLID_SWATCH_TAG = "swatch_bg_brandSolid"

/** Registers the color screen for [ColorPreviewKey]. */
fun EntryProviderScope<NavKey>.colorPreviewEntry(onBack: () -> Unit) {
    entry<ColorPreviewKey> { PreviewScaffold(title = "Color", onBack = onBack) { ColorPreviewContent() } }
}

/** Every bg, fg and stroke color token as a swatch with its name; draws no background of its own. */
@Composable
fun ColorPreviewContent() {
    PreviewBody {
        PreviewSectionTitle("bg")
        bgSwatches().forEach { (name, color) ->
            Swatch(name, color, tag = if (name == "brandSolid") BRAND_SOLID_SWATCH_TAG else null)
        }
        PreviewSectionTitle("fg")
        fgSwatches().forEach { (name, color) -> Swatch(name, color) }
        PreviewSectionTitle("stroke")
        strokeSwatches().forEach { (name, color) -> Swatch(name, color) }
    }
}

@Composable
private fun Swatch(
    name: String,
    color: Color,
    tag: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
    ) {
        Box(
            Modifier
                .then(if (tag != null) Modifier.testTag(tag) else Modifier)
                .size(width = PushlogTheme.dimension.x12, height = PushlogTheme.dimension.x6)
                .background(color)
                .border(SWATCH_BORDER_WIDTH, PushlogTheme.color.stroke.neutralMuted),
        )
        Text(name, style = PushlogTheme.typography.t3Regular, color = PushlogTheme.color.fg.neutral)
    }
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

@PushlogPreview
@Composable
private fun ColorPreviewContentPreview() {
    PushlogTheme {
        ColorPreviewContent()
    }
}
