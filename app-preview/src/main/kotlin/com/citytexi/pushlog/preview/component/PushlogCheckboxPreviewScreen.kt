package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckbox
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckboxWeight
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkSize
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkTone
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckmarkVariant
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A longer label that wraps onto a second line."

/** Registers the PushlogCheckbox screen for [PushlogCheckboxPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogCheckboxPreviewEntry(onBack: () -> Unit) {
    entry<PushlogCheckboxPreviewKey> {
        PreviewScaffold(title = "PushlogCheckbox", onBack = onBack) { PushlogCheckboxPreviewContent() }
    }
}

/** PushlogCheckbox per variant and tone, per size and weight, and with a long label; draws no background of its own. */
@Composable
fun PushlogCheckboxPreviewContent() {
    PreviewBody {
        PushlogCheckmarkVariant.entries.forEach { variant ->
            PushlogCheckmarkTone.entries.forEach { tone -> CheckboxVariantSection(variant, tone) }
        }
        PushlogCheckmarkSize.entries.forEach { CheckboxSizeSection(it) }
        CheckboxLongLabel()
    }
}

/** One toggling row, one Indeterminate row, then disabled On and Off rows. */
@Composable
internal fun CheckboxVariantSection(
    variant: PushlogCheckmarkVariant,
    tone: PushlogCheckmarkTone,
) {
    var checked by remember { mutableStateOf(true) }
    PreviewSectionTitle("${variant.name} · tone ${tone.name}")
    PushlogCheckbox(checked = checked, onCheckedChange = { checked = it }, label = "Toggle", variant = variant, tone = tone)
    PushlogCheckbox(state = ToggleableState.Indeterminate, onClick = {}, label = "Indeterminate", variant = variant, tone = tone)
    PushlogCheckbox(checked = true, onCheckedChange = {}, label = "Disabled on", variant = variant, tone = tone, enabled = false)
    PushlogCheckbox(checked = false, onCheckedChange = {}, label = "Disabled off", variant = variant, tone = tone, enabled = false)
}

@Composable
internal fun CheckboxSizeSection(size: PushlogCheckmarkSize) {
    PreviewSectionTitle("size ${size.name}")
    PushlogCheckboxWeight.entries.forEach { weight ->
        var checked by remember { mutableStateOf(false) }
        PushlogCheckbox(
            checked = checked,
            onCheckedChange = { checked = it },
            label = "weight ${weight.name}",
            size = size,
            weight = weight,
        )
    }
}

@Composable
internal fun CheckboxLongLabel() {
    var checked by remember { mutableStateOf(true) }
    PreviewSectionTitle("long label")
    PushlogCheckbox(checked = checked, onCheckedChange = { checked = it }, label = LONG_LABEL, modifier = Modifier.width(200.dp))
}
