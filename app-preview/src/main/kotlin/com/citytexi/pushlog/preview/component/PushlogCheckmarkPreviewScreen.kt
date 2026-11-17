package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.state.ToggleableState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmark
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkSize
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkTone
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkVariant
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val States = listOf(ToggleableState.Off, ToggleableState.On, ToggleableState.Indeterminate)

/** Registers the PushlogCheckmark screen for [PushlogCheckmarkPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogCheckmarkPreviewEntry(onBack: () -> Unit) {
    entry<PushlogCheckmarkPreviewKey> {
        PreviewScaffold(title = "PushlogCheckmark", onBack = onBack) { PushlogCheckmarkPreviewContent() }
    }
}

/** PushlogCheckmark per variant, tone and state, then per size; draws no background of its own. */
@Composable
fun PushlogCheckmarkPreviewContent() {
    PreviewBody {
        PushlogCheckmarkVariant.entries.forEach { CheckmarkVariantSection(it) }
        PushlogCheckmarkSize.entries.forEach { CheckmarkSizeSection(it) }
    }
}

/** One row per tone: Off, On, Indeterminate, disabled Off, disabled On. */
@Composable
private fun CheckmarkVariantSection(variant: PushlogCheckmarkVariant) {
    PreviewSectionTitle(variant.name)
    PushlogCheckmarkTone.entries.forEach { tone ->
        PreviewSectionTitle("tone ${tone.name}")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
            States.forEach { PushlogCheckmark(state = it, variant = variant, tone = tone) }
            PushlogCheckmark(state = ToggleableState.Off, variant = variant, tone = tone, enabled = false)
            PushlogCheckmark(state = ToggleableState.On, variant = variant, tone = tone, enabled = false)
        }
    }
}

@Composable
private fun CheckmarkSizeSection(size: PushlogCheckmarkSize) {
    PreviewSectionTitle("size ${size.name}")
    PushlogCheckmarkVariant.entries.forEach { variant ->
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
            States.forEach { PushlogCheckmark(state = it, variant = variant, size = size) }
        }
    }
}

// The preview canvas is 320x470 and clips, so each preview holds one variant or size; 2x font fits too.
@PushlogPreview
@Composable
private fun PushlogCheckmarkSquarePreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckmarkVariantSection(PushlogCheckmarkVariant.SQUARE) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckmarkGhostPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckmarkVariantSection(PushlogCheckmarkVariant.GHOST) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckmarkSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckmarkSizeSection(PushlogCheckmarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckmarkSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { CheckmarkSizeSection(PushlogCheckmarkSize.LARGE) }
    }
}
