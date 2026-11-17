package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInput
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant
import com.citytexi.pushlog.core.designsystem.component.textinput.graphemeCount
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val MAX_BIO = 10

/** Registers the PushlogTextInput screen for [PushlogTextInputPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogTextInputPreviewEntry(onBack: () -> Unit) {
    entry<PushlogTextInputPreviewKey> {
        PreviewScaffold(title = "PushlogTextInput", onBack = onBack) { PushlogTextInputPreviewContent() }
    }
}

/** PushlogTextInput per variant and state, with prefix and suffix, and inside a field; draws no background of its own. */
@Composable
fun PushlogTextInputPreviewContent() {
    PreviewBody {
        PushlogTextInputVariant.entries.forEach { StateSection(it) }
        AffixSection()
        FieldSection()
    }
}

@Composable
private fun StateSection(variant: PushlogTextInputVariant) {
    PreviewSectionTitle("${variant.name.lowercase()} states")
    PushlogTextInput(remember { TextFieldState() }, variant = variant, placeholder = "Placeholder")
    PushlogTextInput(remember { TextFieldState("Value") }, variant = variant)
    PushlogTextInput(remember { TextFieldState("Focused") }, variant = variant, interactionSource = rememberFocused())
    PushlogTextInput(remember { TextFieldState("Invalid") }, variant = variant, invalid = true)
    PushlogTextInput(remember { TextFieldState("Read only") }, variant = variant, readOnly = true)
    PushlogTextInput(remember { TextFieldState() }, variant = variant, enabled = false, placeholder = "Disabled")
}

@Composable
private fun AffixSection() {
    PreviewSectionTitle("prefix and suffix")
    val search = rememberVectorPainter(PreviewSampleIcons.Plus)
    val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
    PushlogTextInput(
        remember { TextFieldState("10000") },
        prefixIcon = search,
        prefixText = "KRW",
        suffixText = "won",
        suffixIcon = down,
    )
}

@Composable
private fun FieldSection() {
    PreviewSectionTitle("inside a field")
    val state = remember { TextFieldState("Hello") }
    PushlogField(label = "Bio", characterCount = state.graphemeCount, maxCharacterCount = MAX_BIO) {
        PushlogTextInput(state, maxGraphemeCount = MAX_BIO)
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
private fun PushlogTextInputOutlinePreview() {
    PushlogPreviewTheme { PreviewBody { StateSection(PushlogTextInputVariant.OUTLINE) } }
}

@PushlogPreview
@Composable
private fun PushlogTextInputUnderlinePreview() {
    PushlogPreviewTheme { PreviewBody { StateSection(PushlogTextInputVariant.UNDERLINE) } }
}

@PushlogPreview
@Composable
private fun PushlogTextInputAffixPreview() {
    PushlogPreviewTheme { PreviewBody { AffixSection() } }
}

@PushlogPreview
@Composable
private fun PushlogTextInputFieldPreview() {
    PushlogPreviewTheme { PreviewBody { FieldSection() } }
}
