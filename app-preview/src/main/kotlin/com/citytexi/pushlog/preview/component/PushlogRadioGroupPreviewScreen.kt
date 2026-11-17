package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadio
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadioGroup
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val SortLabels = listOf("Newest first", "Oldest first", "By app")

/** Registers the PushlogRadioGroup screen for [PushlogRadioGroupPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogRadioGroupPreviewEntry(onBack: () -> Unit) {
    entry<PushlogRadioGroupPreviewKey> {
        PreviewScaffold(title = "PushlogRadioGroup", onBack = onBack) { PushlogRadioGroupPreviewContent() }
    }
}

/** PushlogRadioGroup with three options and a single selection; draws no background of its own. */
@Composable
fun PushlogRadioGroupPreviewContent() {
    PreviewBody { RadioGroupSection() }
}

@Composable
private fun RadioGroupSection() {
    var selected by remember { mutableIntStateOf(0) }
    PreviewSectionTitle("single choice")
    PushlogRadioGroup {
        SortLabels.forEachIndexed { index, label ->
            PushlogRadio(selected = selected == index, onClick = { selected = index }, label = label)
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioGroupPreview() {
    PushlogTheme {
        PreviewBody { RadioGroupSection() }
    }
}
