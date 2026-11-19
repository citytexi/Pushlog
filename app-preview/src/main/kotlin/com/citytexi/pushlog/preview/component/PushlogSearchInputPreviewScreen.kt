package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.searchinput.PushlogSearchInput
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogSearchInput screen for [PushlogSearchInputPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogSearchInputPreviewEntry(onBack: () -> Unit) {
    entry<PushlogSearchInputPreviewKey> {
        PreviewScaffold(title = "PushlogSearchInput", onBack = onBack) {
            PushlogSearchInputPreviewContent()
        }
    }
}

/** PushlogSearchInput per state, plus a live submit demo; draws no background of its own. */
@Composable
fun PushlogSearchInputPreviewContent() {
    PreviewBody {
        StatesSection()
        SubmitSection()
    }
}

@Composable
private fun StatesSection() {
    PreviewSectionTitle("states")
    PushlogSearchInput(remember { TextFieldState() }, onSubmit = {}, placeholder = "Search")
    PushlogSearchInput(remember { TextFieldState("Coffee") }, onSubmit = {})
    PushlogSearchInput(remember { TextFieldState() }, onSubmit = {}, placeholder = "Disabled", enabled = false)
}

@Composable
private fun SubmitSection() {
    PreviewSectionTitle("submit")
    var submitted by remember { mutableStateOf<String?>(null) }
    PushlogSearchInput(remember { TextFieldState() }, onSubmit = { submitted = it }, placeholder = "Type and press search")
    Text(
        submitted?.let { "Submitted: \"$it\"" } ?: "Nothing submitted yet",
        style = PushlogTheme.typography.t4Medium,
        color = PushlogTheme.color.fg.neutralMuted,
    )
}

@PushlogPreview
@Composable
private fun PushlogSearchInputStatesPreview() {
    PushlogPreviewTheme { PreviewBody { StatesSection() } }
}

@PushlogPreview
@Composable
private fun PushlogSearchInputSubmitPreview() {
    PushlogPreviewTheme { PreviewBody { SubmitSection() } }
}
