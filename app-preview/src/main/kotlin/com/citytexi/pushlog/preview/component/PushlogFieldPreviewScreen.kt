package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInput
import com.citytexi.pushlog.core.designsystem.component.textinput.graphemeCount
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogField screen for [PushlogFieldPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogFieldPreviewEntry(onBack: () -> Unit) {
    entry<PushlogFieldPreviewKey> {
        PreviewScaffold(title = "PushlogField", onBack = onBack) { PushlogFieldPreviewContent() }
    }
}

/** PushlogField around a PushlogTextInput in each state; draws no background of its own. */
@Composable
fun PushlogFieldPreviewContent() {
    PreviewBody { FieldSections() }
}

@Composable
private fun FieldSections() {
    PreviewSectionTitle("label and description")
    PushlogField(label = "Name", description = "Use your real name") { PushlogTextInput(remember { TextFieldState() }) }
    PreviewSectionTitle("required and indicator text")
    PushlogField(
        label = "Name",
        showRequiredIndicator = true,
        indicatorText = "Optional",
    ) { PushlogTextInput(remember { TextFieldState() }) }
    PreviewSectionTitle("invalid")
    PushlogField(label = "Name", description = "Digits only", errorMessage = "Required", invalid = true) {
        PushlogTextInput(remember { TextFieldState() })
    }
    PreviewSectionTitle("character count")
    val bio = remember { TextFieldState("abc") }
    PushlogField(label = "Bio", characterCount = bio.graphemeCount, maxCharacterCount = 40) {
        PushlogTextInput(bio, maxGraphemeCount = 40)
    }
}

@PushlogPreview
@Composable
private fun PushlogFieldPreview() {
    PushlogPreviewTheme {
        PreviewBody { FieldSections() }
    }
}
