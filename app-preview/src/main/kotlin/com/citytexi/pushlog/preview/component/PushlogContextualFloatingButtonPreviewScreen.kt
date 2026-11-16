package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.button.PushlogContextualFloatingButton
import com.citytexi.pushlog.core.designsystem.component.button.PushlogContextualFloatingButtonVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A much longer floating button label that has to be cut off with an ellipsis"

/** Registers the PushlogContextualFloatingButton screen for [PushlogContextualFloatingButtonPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogContextualFloatingButtonPreviewEntry(onBack: () -> Unit) {
    entry<PushlogContextualFloatingButtonPreviewKey> {
        PreviewScaffold(title = "PushlogContextualFloatingButton", onBack = onBack) { PushlogContextualFloatingButtonPreviewContent() }
    }
}

/** PushlogContextualFloatingButton per variant and state, text only, icon only, and with a long label; draws no background of its own. */
@Composable
fun PushlogContextualFloatingButtonPreviewContent() {
    PreviewBody {
        FloatingButtonVariants()
        FloatingButtonLayouts()
    }
}

@Composable
private fun FloatingButtonVariants() {
    val arrow = rememberVectorPainter(PreviewSampleIcons.ArrowUp)
    PushlogContextualFloatingButtonVariant.entries.forEach { variant ->
        PreviewSectionTitle(variant.name)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
            PushlogContextualFloatingButton(text = "To top", onClick = {}, variant = variant, prefixIcon = arrow)
            PushlogContextualFloatingButton(text = "Disabled", onClick = {}, variant = variant, enabled = false)
            PushlogContextualFloatingButton(text = "Loading", onClick = {}, variant = variant, loading = true)
        }
    }
}

@Composable
private fun FloatingButtonLayouts() {
    val arrow = rememberVectorPainter(PreviewSampleIcons.ArrowUp)
    PreviewSectionTitle("text only")
    PushlogContextualFloatingButton(text = "To top", onClick = {})
    PreviewSectionTitle("icon only")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
        PushlogContextualFloatingButton(icon = arrow, contentDescription = "To top", onClick = {})
        PushlogContextualFloatingButton(icon = arrow, contentDescription = "To top", onClick = {}, enabled = false)
        PushlogContextualFloatingButton(icon = arrow, contentDescription = "To top", onClick = {}, loading = true)
    }
    PreviewSectionTitle("long label")
    PushlogContextualFloatingButton(text = LONG_LABEL, onClick = {}, modifier = Modifier.width(200.dp), prefixIcon = arrow)
}

// The preview canvas is 320x470 and clips, so each section gets its own preview.
@PushlogPreview
@Composable
private fun PushlogContextualFloatingButtonVariantsPreview() {
    PushlogTheme {
        PreviewBody { FloatingButtonVariants() }
    }
}

@PushlogPreview
@Composable
private fun PushlogContextualFloatingButtonLayoutsPreview() {
    PushlogTheme {
        PreviewBody { FloatingButtonLayouts() }
    }
}
