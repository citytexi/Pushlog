package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.component.inputbutton.PushlogInputButton
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

// Stand-in for the picker a real screen would open.
private val SAMPLE_DATES = listOf("1990.01.01", "2000.12.31", "2024.02.29")

/** Registers the PushlogInputButton screen for [PushlogInputButtonPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogInputButtonPreviewEntry(onBack: () -> Unit) {
    entry<PushlogInputButtonPreviewKey> {
        PreviewScaffold(title = "PushlogInputButton", onBack = onBack) { PushlogInputButtonPreviewContent() }
    }
}

/** PushlogInputButton per state, with prefix and suffix, and a live demo inside a field; draws no background of its own. */
@Composable
fun PushlogInputButtonPreviewContent() {
    PreviewBody {
        StatesSection()
        AffixSection()
        FieldSection()
    }
}

@Composable
private fun StatesSection() {
    PreviewSectionTitle("states")
    PushlogInputButton(null, onClick = {}, placeholder = "Pick a date", contentDescription = "Birthday")
    PushlogInputButton("1990.01.01", onClick = {}, contentDescription = "Birthday")
    PushlogInputButton("1990.01.01", onClick = {}, onClear = {}, contentDescription = "Birthday")
    PushlogInputButton("Invalid", onClick = {}, contentDescription = "Birthday", invalid = true)
    PushlogInputButton("Read only", onClick = {}, contentDescription = "Birthday", readOnly = true)
    PushlogInputButton("Disabled", onClick = {}, contentDescription = "Birthday", enabled = false)
}

@Composable
private fun AffixSection() {
    PreviewSectionTitle("prefix and suffix")
    PushlogInputButton(
        "10000",
        onClick = {},
        prefixIcon = rememberVectorPainter(PreviewSampleIcons.Plus),
        prefixText = "KRW",
        suffixText = "won",
        suffixIcon = rememberVectorPainter(PreviewSampleIcons.ChevronDown),
        contentDescription = "Amount",
    )
}

@Composable
private fun FieldSection() {
    PreviewSectionTitle("inside a field (tap to cycle, clear to reset)")
    var index by remember { mutableIntStateOf(0) }
    var value by remember { mutableStateOf<String?>(null) }
    PushlogField(label = "Birthday", description = "Tap to pick a sample date") {
        PushlogInputButton(
            value = value,
            onClick = {
                value = SAMPLE_DATES[index]
                index = (index + 1) % SAMPLE_DATES.size
            },
            placeholder = "Pick a date",
            onClear = { value = null },
            suffixIcon = rememberVectorPainter(PreviewSampleIcons.ChevronDown),
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogInputButtonStatesPreview() {
    PushlogPreviewTheme { PreviewBody { StatesSection() } }
}

@PushlogPreview
@Composable
private fun PushlogInputButtonAffixPreview() {
    PushlogPreviewTheme { PreviewBody { AffixSection() } }
}

@PushlogPreview
@Composable
private fun PushlogInputButtonFieldPreview() {
    PushlogPreviewTheme { PreviewBody { FieldSection() } }
}
