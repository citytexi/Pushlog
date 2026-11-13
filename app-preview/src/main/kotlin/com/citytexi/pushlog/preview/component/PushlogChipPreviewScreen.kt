package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.PushlogChip
import com.citytexi.pushlog.core.designsystem.component.PushlogChipSize
import com.citytexi.pushlog.core.designsystem.component.PushlogChipVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A much longer chip label that has to be cut off with an ellipsis"

/** Registers the PushlogChip screen for [PushlogChipPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogChipPreviewEntry(onBack: () -> Unit) {
    entry<PushlogChipPreviewKey> {
        PreviewScaffold(title = "PushlogChip", onBack = onBack) { PushlogChipPreviewContent() }
    }
}

/** PushlogChip per variant and state, per size and icon, and with a long label; draws no background of its own. */
@Composable
fun PushlogChipPreviewContent() {
    val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
    val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
    PreviewBody {
        PushlogChipVariant.entries.forEach { variant ->
            var selected by remember { mutableStateOf(false) }
            PreviewSectionTitle(variant.name)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                PushlogChip(text = "Button", onClick = {}, variant = variant, suffixIcon = down)
                PushlogChip(text = "Toggle", onClick = { selected = !selected }, variant = variant, selected = selected, prefixIcon = plus)
                PushlogChip(text = "Disabled", onClick = {}, variant = variant, enabled = false)
                PushlogChip(text = "Selected disabled", onClick = {}, variant = variant, selected = true, enabled = false)
            }
        }
        PushlogChipSize.entries.forEach { size ->
            PreviewSectionTitle("size ${size.name}")
            Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                PushlogChip(text = "Chip", onClick = {}, size = size)
                PushlogChip(text = "Chip", onClick = {}, size = size, prefixIcon = plus)
                PushlogChip(text = "Chip", onClick = {}, size = size, suffixIcon = down)
            }
        }
        PreviewSectionTitle("long label")
        PushlogChip(text = LONG_LABEL, onClick = {}, modifier = Modifier.width(200.dp))
    }
}

@PushlogPreview
@Composable
private fun PushlogChipPreviewContentPreview() {
    PushlogTheme {
        PushlogChipPreviewContent()
    }
}
