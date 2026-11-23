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
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogResidentNumberDigits
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogResidentNumberInput
import com.citytexi.pushlog.core.designsystem.component.segmentedtextinput.PushlogResidentNumberState
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the PushlogResidentNumberInput screen for [PushlogResidentNumberInputPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogResidentNumberInputPreviewEntry(onBack: () -> Unit) {
    entry<PushlogResidentNumberInputPreviewKey> {
        PreviewScaffold(title = "PushlogResidentNumberInput", onBack = onBack) { PushlogResidentNumberInputPreviewContent() }
    }
}

/** PushlogResidentNumberInput per format with a format picker, per state, and inside a field; draws no background of its own. */
@Composable
fun PushlogResidentNumberInputPreviewContent() {
    PreviewBody {
        FormatPickerSection()
        FormatsSection()
        StatesSection()
        FieldSection()
    }
}

@Composable
private fun FormatPickerSection() {
    PreviewSectionTitle("pick a format")
    var selected by rememberSaveable { mutableIntStateOf(PushlogResidentNumberDigits.SEVEN.ordinal) }
    val digits = PushlogResidentNumberDigits.entries[selected]
    PushlogSegmentedControl(
        options = PushlogResidentNumberDigits.entries.map { PushlogSegmentedControlOption(it.name) },
        selectedIndex = selected,
        onSelectedIndexChange = { selected = it },
    )
    val state = rememberSaveable(digits, saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState(digits) }
    PushlogResidentNumberInput(state)
    // Only lengths: the masked back part must not reach the screen.
    Text(
        text = "filled: " + state.values.joinToString(" / ") { it.length.toString() },
        style = PushlogTheme.typography.t4Regular,
        color = PushlogTheme.color.fg.neutralSubtle,
    )
}

@Composable
private fun FormatsSection() {
    PushlogResidentNumberDigits.entries.forEach { digits ->
        PreviewSectionTitle(digits.name)
        PushlogResidentNumberInput(
            rememberSaveable(digits, saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState(digits) },
        )
    }
}

@Composable
private fun StatesSection() {
    PreviewSectionTitle("invalid")
    PushlogResidentNumberInput(rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState() }, invalid = true)
    PreviewSectionTitle("read only")
    PushlogResidentNumberInput(rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState() }, readOnly = true)
    PreviewSectionTitle("disabled")
    PushlogResidentNumberInput(rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState() }, enabled = false)
}

@Composable
private fun FieldSection() {
    PreviewSectionTitle("inside a field")
    val state =
        rememberSaveable(saver = PushlogResidentNumberState.Saver) { PushlogResidentNumberState(PushlogResidentNumberDigits.THIRTEEN) }
    val birthIncomplete = state.values[0].length in 1 until BIRTH_LENGTH
    PushlogField(
        label = "Resident registration number",
        showRequiredIndicator = true,
        description = "The back part is masked",
        errorMessage = "Enter all 6 birth date digits",
        invalid = birthIncomplete,
    ) {
        PushlogResidentNumberInput(state)
    }
}

private const val BIRTH_LENGTH = 6

@PushlogPreview
@Composable
private fun PushlogResidentNumberInputFormatsPreview() {
    PushlogPreviewTheme { PreviewBody { FormatsSection() } }
}

@PushlogPreview
@Composable
private fun PushlogResidentNumberInputStatesPreview() {
    PushlogPreviewTheme { PreviewBody { StatesSection() } }
}

@PushlogPreview
@Composable
private fun PushlogResidentNumberInputFieldPreview() {
    PushlogPreviewTheme { PreviewBody { FieldSection() } }
}
