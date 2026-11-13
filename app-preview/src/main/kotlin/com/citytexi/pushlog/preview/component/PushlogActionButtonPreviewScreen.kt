package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
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
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    PreviewBody {
        PushlogActionButtonVariant.entries.forEach { variant ->
            PreviewSectionTitle(variant.name)
            Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                PushlogActionButton(text = "Action", onClick = {}, variant = variant)
                PushlogActionButton(text = "Disabled", onClick = {}, variant = variant, enabled = false)
                PushlogActionButton(text = "Loading", onClick = {}, variant = variant, loading = true)
            }
        }
        PushlogActionButtonSize.entries.forEach { size ->
            PreviewSectionTitle("size ${size.name}")
            Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                PushlogActionButton(text = "Action", onClick = {}, size = size)
                PushlogActionButton(text = "Action", onClick = {}, size = size, prefixIcon = plus)
                PushlogActionButton(icon = plus, contentDescription = "Add", onClick = {}, size = size)
            }
        }
        PreviewSectionTitle("long label")
        PushlogActionButton(text = LONG_LABEL, onClick = {})
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonPreviewContentPreview() {
    PushlogTheme {
        PushlogActionButtonPreviewContent()
    }
}
