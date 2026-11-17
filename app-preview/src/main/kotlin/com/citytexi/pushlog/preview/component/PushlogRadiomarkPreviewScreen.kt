package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomark
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkSize
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkTone
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogRadiomark screen for [PushlogRadiomarkPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogRadiomarkPreviewEntry(onBack: () -> Unit) {
    entry<PushlogRadiomarkPreviewKey> {
        PreviewScaffold(title = "PushlogRadiomark", onBack = onBack) { PushlogRadiomarkPreviewContent() }
    }
}

/** PushlogRadiomark per tone and state, then per size; draws no background of its own. */
@Composable
fun PushlogRadiomarkPreviewContent() {
    PreviewBody {
        PushlogRadiomarkTone.entries.forEach { RadiomarkToneSection(it) }
        PushlogRadiomarkSize.entries.forEach { RadiomarkSizeSection(it) }
    }
}

/** Off, selected, disabled off, disabled selected. */
@Composable
private fun RadiomarkToneSection(tone: PushlogRadiomarkTone) {
    PreviewSectionTitle("tone ${tone.name}")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
        PushlogRadiomark(selected = false, tone = tone)
        PushlogRadiomark(selected = true, tone = tone)
        PushlogRadiomark(selected = false, tone = tone, enabled = false)
        PushlogRadiomark(selected = true, tone = tone, enabled = false)
    }
}

/** Off, selected, disabled selected; the disabled dot is larger. */
@Composable
private fun RadiomarkSizeSection(size: PushlogRadiomarkSize) {
    PreviewSectionTitle("size ${size.name}")
    FlowRow(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
        PushlogRadiomark(selected = false, size = size)
        PushlogRadiomark(selected = true, size = size)
        PushlogRadiomark(selected = true, size = size, enabled = false)
    }
}

// The preview canvas is 320x470 and clips, so each preview holds one tone or size; 2x font fits too.
@PushlogPreview
@Composable
private fun PushlogRadiomarkNeutralPreview() {
    PushlogTheme {
        PreviewBody { RadiomarkToneSection(PushlogRadiomarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadiomarkBrandPreview() {
    PushlogTheme {
        PreviewBody { RadiomarkToneSection(PushlogRadiomarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadiomarkSizeMediumPreview() {
    PushlogTheme {
        PreviewBody { RadiomarkSizeSection(PushlogRadiomarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadiomarkSizeLargePreview() {
    PushlogTheme {
        PreviewBody { RadiomarkSizeSection(PushlogRadiomarkSize.LARGE) }
    }
}
