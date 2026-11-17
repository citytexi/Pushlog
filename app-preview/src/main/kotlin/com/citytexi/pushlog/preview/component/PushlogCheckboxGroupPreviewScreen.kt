package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.state.ToggleableState
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckbox
import com.citytexi.pushlog.core.designsystem.component.checkbox.PushlogCheckboxGroup
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val ChildLabels = listOf("Messenger", "Shopping", "Banking")

/** Registers the PushlogCheckboxGroup screen for [PushlogCheckboxGroupPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogCheckboxGroupPreviewEntry(onBack: () -> Unit) {
    entry<PushlogCheckboxGroupPreviewKey> {
        PreviewScaffold(title = "PushlogCheckboxGroup", onBack = onBack) { PushlogCheckboxGroupPreviewContent() }
    }
}

/** PushlogCheckboxGroup with a "select all" parent driven by its children; draws no background of its own. */
@Composable
fun PushlogCheckboxGroupPreviewContent() {
    PreviewBody { CheckboxGroupSection() }
}

/** The parent state is computed here from the children: all, none, or Indeterminate for a partial pick. */
@Composable
private fun CheckboxGroupSection() {
    val children = remember { mutableStateListOf(true, false, false) }
    val parent =
        when {
            children.all { it } -> ToggleableState.On
            children.none { it } -> ToggleableState.Off
            else -> ToggleableState.Indeterminate
        }
    PreviewSectionTitle("select all")
    PushlogCheckboxGroup {
        PushlogCheckbox(
            state = parent,
            onClick = {
                val next = parent != ToggleableState.On
                children.indices.forEach { children[it] = next }
            },
            label = "Select all",
        )
        ChildLabels.forEachIndexed { index, label ->
            PushlogCheckbox(checked = children[index], onCheckedChange = { children[index] = it }, label = label)
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxGroupPreview() {
    PushlogTheme {
        PreviewBody { CheckboxGroupSection() }
    }
}
