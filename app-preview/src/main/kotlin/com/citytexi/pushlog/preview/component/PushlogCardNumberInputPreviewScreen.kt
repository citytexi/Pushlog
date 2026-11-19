package com.citytexi.pushlog.preview.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.component.segmentedcontrol.PushlogSegmentedControl
import com.citytexi.pushlog.core.designsystem.component.segmentedcontrol.PushlogSegmentedControlOption
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogCardNumberInput
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogCardNumberMaskedDigits
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogCardNumberState
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogCardNumberInput screen for [PushlogCardNumberInputPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogCardNumberInputPreviewEntry(onBack: () -> Unit) {
    entry<PushlogCardNumberInputPreviewKey> {
        PreviewScaffold(title = "PushlogCardNumberInput", onBack = onBack) { PushlogCardNumberInputPreviewContent() }
    }
}

/** PushlogCardNumberInput per masked length with a picker, with a suffix, per state, and inside a field; draws no background of its own. */
@Composable
fun PushlogCardNumberInputPreviewContent() {
    PreviewBody {
        MaskPickerSection()
        MaskSection()
        SuffixSection()
        StatesSection()
        FieldSection()
    }
}

@Composable
private fun MaskPickerSection() {
    PreviewSectionTitle("pick masked digits")
    var selected by rememberSaveable { mutableIntStateOf(PushlogCardNumberMaskedDigits.EIGHT.ordinal) }
    val masked = PushlogCardNumberMaskedDigits.entries[selected]
    PushlogSegmentedControl(
        options = PushlogCardNumberMaskedDigits.entries.map { PushlogSegmentedControlOption(it.name) },
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
    )
    val state = rememberSaveable(masked, saver = PushlogCardNumberState.Saver) { PushlogCardNumberState(masked) }
    PushlogCardNumberInput(state)
    // Only lengths: masked groups must not reach the screen.
    Text(
        text = "filled: " + state.values.joinToString(" / ") { it.length.toString() },
        style = PushlogTheme.typography.t4Regular,
        color = PushlogTheme.color.fg.neutralSubtle,
    )
}

@Composable
private fun MaskSection() {
    PushlogCardNumberMaskedDigits.entries.forEach { masked ->
        PreviewSectionTitle(masked.name)
        PushlogCardNumberInput(rememberSaveable(masked, saver = PushlogCardNumberState.Saver) { PushlogCardNumberState(masked) })
    }
}

@Composable
private fun SuffixSection() {
    PreviewSectionTitle("suffix")
    PushlogCardNumberInput(
        state = rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() },
        suffix = CardBrand,
    )
}

// Stand-in for a card issuer logo.
private val CardBrand: @Composable () -> Unit = {
    Text(text = "CARD", style = PushlogTheme.typography.t3Bold, color = PushlogTheme.color.fg.neutralMuted)
}

@Composable
private fun StatesSection() {
    PreviewSectionTitle("invalid")
    PushlogCardNumberInput(rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() }, invalid = true)
    PreviewSectionTitle("read only")
    PushlogCardNumberInput(rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() }, readOnly = true)
    PreviewSectionTitle("disabled")
    PushlogCardNumberInput(rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() }, enabled = false)
}

@Composable
private fun FieldSection() {
    PreviewSectionTitle("inside a field")
    val state = rememberSaveable(saver = PushlogCardNumberState.Saver) { PushlogCardNumberState() }
    PushlogField(label = "Card number", description = "Paste into the first group to fill up to the masked part") {
        PushlogCardNumberInput(state, suffix = CardBrand)
    }
}

@PushlogPreview
@Composable
private fun PushlogCardNumberInputMaskPreview() {
    PushlogPreviewTheme { PreviewBody { MaskSection() } }
}

@PushlogPreview
@Composable
private fun PushlogCardNumberInputSuffixPreview() {
    PushlogPreviewTheme { PreviewBody { SuffixSection() } }
}

@PushlogPreview
@Composable
private fun PushlogCardNumberInputStatesPreview() {
    PushlogPreviewTheme { PreviewBody { StatesSection() } }
}

@PushlogPreview
@Composable
private fun PushlogCardNumberInputFieldPreview() {
    PushlogPreviewTheme { PreviewBody { FieldSection() } }
}
