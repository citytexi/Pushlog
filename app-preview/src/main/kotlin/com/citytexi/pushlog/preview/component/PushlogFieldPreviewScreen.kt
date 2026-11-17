package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.field.PushlogField
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
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

/** PushlogField around a placeholder slot in each state; draws no background of its own. */
@Composable
fun PushlogFieldPreviewContent() {
    PreviewBody { FieldSections() }
}

@Composable
private fun FieldSections() {
    PreviewSectionTitle("label and description")
    PushlogField(label = "Name", description = "Use your real name") { Slot() }
    PreviewSectionTitle("required and indicator text")
    PushlogField(label = "Name", showRequiredIndicator = true, indicatorText = "Optional") { Slot() }
    PreviewSectionTitle("invalid")
    PushlogField(label = "Name", description = "Digits only", errorMessage = "Required", invalid = true) { Slot() }
    PreviewSectionTitle("character count")
    PushlogField(label = "Bio", characterCount = 3, maxCharacterCount = 40) { Slot() }
}

// Stands in for the input until PushlogTextInput exists.
@Composable
private fun Slot() {
    Box(Modifier.fillMaxWidth().height(PushlogTheme.dimension.x13).background(PushlogTheme.color.bg.neutralWeak))
}

@PushlogPreview
@Composable
private fun PushlogFieldPreview() {
    PushlogPreviewTheme {
        PreviewBody { FieldSections() }
    }
}
