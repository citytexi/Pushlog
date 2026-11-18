package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextArea
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant
import com.citytexi.pushlog.core.designsystem.component.textinput.graphemeCount
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val MAX_BIO = 100

/** Registers the PushlogTextArea screen for [PushlogTextAreaPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogTextAreaPreviewEntry(onBack: () -> Unit) {
    entry<PushlogTextAreaPreviewKey> {
        PreviewScaffold(title = "PushlogTextArea", onBack = onBack) { PushlogTextAreaPreviewContent() }
    }
}

/** PushlogTextArea per variant and state, and inside a field with a character count; draws no background of its own. */
@Composable
fun PushlogTextAreaPreviewContent() {
    PreviewBody {
        PushlogTextInputVariant.entries.forEach { DefaultSection(it) }
        InvalidSection()
        DisabledSection()
        FieldSection()
    }
}

@Composable
private fun DefaultSection(variant: PushlogTextInputVariant) {
    PreviewSectionTitle("${variant.name.lowercase()} default")
    PushlogTextArea(remember { TextFieldState() }, variant = variant, placeholder = "Placeholder")
    PushlogTextArea(remember { TextFieldState("Line 1\nLine 2\nLine 3\nLine 4\nLine 5") }, variant = variant)
    PushlogTextArea(remember { TextFieldState("Focused") }, variant = variant, interactionSource = rememberFocused())
}

@Composable
private fun InvalidSection() {
    PreviewSectionTitle("invalid")
    PushlogTextArea(remember { TextFieldState("Invalid") }, invalid = true)
}

@Composable
private fun DisabledSection() {
    PreviewSectionTitle("disabled and read only")
    PushlogTextArea(remember { TextFieldState() }, enabled = false, placeholder = "Disabled")
    PushlogTextArea(remember { TextFieldState("Read only") }, readOnly = true)
}

@Composable
private fun FieldSection() {
    PreviewSectionTitle("inside a field with count")
    val state = remember { TextFieldState("Hello") }
    PushlogField(label = "Bio", characterCount = state.graphemeCount, maxCharacterCount = MAX_BIO) {
        PushlogTextArea(state, maxGraphemeCount = MAX_BIO)
    }
}

@Composable
private fun rememberFocused(): MutableInteractionSource {
    val source = remember { MutableInteractionSource() }
    LaunchedEffect(source) { source.emit(FocusInteraction.Focus()) }
    return source
}

@PushlogPreview
@Composable
private fun PushlogTextAreaDefaultPreview() {
    PushlogPreviewTheme { PreviewBody { DefaultSection(PushlogTextInputVariant.OUTLINE) } }
}

@PushlogPreview
@Composable
private fun PushlogTextAreaInvalidPreview() {
    PushlogPreviewTheme { PreviewBody { InvalidSection() } }
}

@PushlogPreview
@Composable
private fun PushlogTextAreaDisabledPreview() {
    PushlogPreviewTheme { PreviewBody { DisabledSection() } }
}

@PushlogPreview
@Composable
private fun PushlogTextAreaFieldPreview() {
    PushlogPreviewTheme { PreviewBody { FieldSection() } }
}
