package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.PushlogActionButton
import com.citytexi.pushlog.core.designsystem.component.PushlogActionButtonSize
import com.citytexi.pushlog.core.designsystem.component.PushlogActionButtonVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A much longer button label that has to wrap on a narrow screen"

/** Registers the PushlogActionButton screen for [PushlogActionButtonPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogActionButtonPreviewEntry(onBack: () -> Unit) {
    entry<PushlogActionButtonPreviewKey> {
        PreviewScaffold(title = "PushlogActionButton", onBack = onBack) { PushlogActionButtonPreviewContent() }
    }
}

/** PushlogActionButton per variant and state, per size and layout, and with a long label; draws no background of its own. */
@Composable
fun PushlogActionButtonPreviewContent() {
    PreviewBody {
        ActionButtonVariants()
        ActionButtonSizes()
        PreviewSectionTitle("long label")
        PushlogActionButton(text = LONG_LABEL, onClick = {})
    }
}

@Composable
private fun ActionButtonVariants() {
    PushlogActionButtonVariant.entries.forEach { variant ->
        PreviewSectionTitle(variant.name)
        Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
            PushlogActionButton(text = "Action", onClick = {}, variant = variant)
            PushlogActionButton(text = "Disabled", onClick = {}, variant = variant, enabled = false)
            PushlogActionButton(text = "Loading", onClick = {}, variant = variant, loading = true)
        }
    }
}

@Composable
private fun ActionButtonSizes() {
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    PushlogActionButtonSize.entries.forEach { size ->
        PreviewSectionTitle("size ${size.name}")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
            PushlogActionButton(text = "Action", onClick = {}, size = size)
            PushlogActionButton(text = "Action", onClick = {}, size = size, prefixIcon = plus)
            PushlogActionButton(icon = plus, contentDescription = "Add", onClick = {}, size = size)
        }
    }
}

// The preview canvas is 320x470 and clips, so each section gets its own preview.
@PushlogPreview
@Composable
private fun PushlogActionButtonVariantsPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonVariants() }
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonSizesPreview() {
    PushlogTheme {
        PreviewBody { ActionButtonSizes() }
    }
}
