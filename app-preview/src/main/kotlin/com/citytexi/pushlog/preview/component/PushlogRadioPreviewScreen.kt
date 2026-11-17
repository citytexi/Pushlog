package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadio
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadioWeight
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkSize
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkTone
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val OptionLabels = listOf("First", "Second", "Third")

private const val LONG_LABEL = "A much longer radio label that wraps onto several lines while the mark stays on the first line."

/** Registers the PushlogRadio screen for [PushlogRadioPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogRadioPreviewEntry(onBack: () -> Unit) {
    entry<PushlogRadioPreviewKey> {
        PreviewScaffold(title = "PushlogRadio", onBack = onBack) { PushlogRadioPreviewContent() }
    }
}

/** PushlogRadio per tone, per size and weight, and with a long label; draws no background of its own. */
@Composable
fun PushlogRadioPreviewContent() {
    PreviewBody {
        PushlogRadiomarkTone.entries.forEach { RadioToneSection(it) }
        PushlogRadiomarkSize.entries.forEach { RadioSizeSection(it) }
        RadioLongLabel()
    }
}

/** Three rows sharing one selection, then disabled selected and unselected rows. */
@Composable
private fun RadioToneSection(tone: PushlogRadiomarkTone) {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("tone ${tone.name}")
    OptionLabels.forEachIndexed { index, label ->
        PushlogRadio(selected = selected == index, onClick = { selected = index }, label = label, tone = tone)
    }
    PushlogRadio(selected = true, onClick = {}, label = "Disabled selected", tone = tone, enabled = false)
    PushlogRadio(selected = false, onClick = {}, label = "Disabled unselected", tone = tone, enabled = false)
}

@Composable
private fun RadioSizeSection(size: PushlogRadiomarkSize) {
    var selected by remember { mutableStateOf(PushlogRadioWeight.REGULAR) }
    PreviewSectionTitle("size ${size.name}")
    PushlogRadioWeight.entries.forEach { weight ->
        PushlogRadio(
            selected = selected == weight,
            onClick = { selected = weight },
            label = "weight ${weight.name}",
            size = size,
            weight = weight,
        )
    }
}

@Composable
private fun RadioLongLabel() {
    PreviewSectionTitle("long label")
    PushlogRadio(selected = true, onClick = {}, label = LONG_LABEL, modifier = Modifier.width(200.dp))
}

// The preview canvas is 320x470 and clips, so each preview holds one tone, one size, or the long label; 2x font fits too.
@PushlogPreview
@Composable
private fun PushlogRadioNeutralPreview() {
    PushlogPreviewTheme {
        PreviewBody { RadioToneSection(PushlogRadiomarkTone.NEUTRAL) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioBrandPreview() {
    PushlogPreviewTheme {
        PreviewBody { RadioToneSection(PushlogRadiomarkTone.BRAND) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioSizeMediumPreview() {
    PushlogPreviewTheme {
        PreviewBody { RadioSizeSection(PushlogRadiomarkSize.MEDIUM) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioSizeLargePreview() {
    PushlogPreviewTheme {
        PreviewBody { RadioSizeSection(PushlogRadiomarkSize.LARGE) }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioLongLabelPreview() {
    PushlogPreviewTheme {
        PreviewBody { RadioLongLabel() }
    }
}
