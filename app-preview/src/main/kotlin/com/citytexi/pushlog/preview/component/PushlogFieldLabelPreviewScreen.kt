package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogFieldLabel
import com.citytexi.pushlog.core.designsystem.component.field.PushlogFieldLabelWeight
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogFieldLabel screen for [PushlogFieldLabelPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogFieldLabelPreviewEntry(onBack: () -> Unit) {
    entry<PushlogFieldLabelPreviewKey> {
        PreviewScaffold(title = "PushlogFieldLabel", onBack = onBack) { PushlogFieldLabelPreviewContent() }
    }
}

/** PushlogFieldLabel in each weight; draws no background of its own. */
@Composable
fun PushlogFieldLabelPreviewContent() {
    PreviewBody { FieldLabelWeightsSection() }
}

@Composable
private fun FieldLabelWeightsSection() {
    PreviewSectionTitle("weights")
    PushlogFieldLabelWeight.entries.forEach { weight ->
        PushlogFieldLabel(text = weight.name, weight = weight)
    }
}

@PushlogPreview
@Composable
private fun PushlogFieldLabelWeightsPreview() {
    PushlogPreviewTheme {
        PreviewBody { FieldLabelWeightsSection() }
    }
}
