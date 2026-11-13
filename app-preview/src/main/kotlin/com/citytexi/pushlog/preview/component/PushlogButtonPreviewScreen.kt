package com.citytexi.pushlog.preview.component

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.PushlogButton
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val LONG_LABEL = "A much longer button label that has to wrap or truncate on a narrow screen"

/** Registers the PushlogButton screen for [PushlogButtonPreviewKey]. */
fun EntryProviderScope<NavKey>.pushlogButtonPreviewEntry(onBack: () -> Unit) {
    entry<PushlogButtonPreviewKey> { PreviewScaffold(title = "PushlogButton", onBack = onBack) { PushlogButtonPreviewContent() } }
}

/** PushlogButton in enabled and disabled states with a short and a long label; draws no background of its own. */
@Composable
fun PushlogButtonPreviewContent() {
    PreviewBody {
        PreviewSectionTitle("enabled")
        PushlogButton(text = "Button", onClick = {})
        PushlogButton(text = LONG_LABEL, onClick = {})
        PreviewSectionTitle("disabled")
        PushlogButton(text = "Button", onClick = {}, enabled = false)
        PushlogButton(text = LONG_LABEL, onClick = {}, enabled = false)
    }
}

@PushlogPreview
@Composable
private fun PushlogButtonPreviewContentPreview() {
    PushlogTheme {
        PushlogButtonPreviewContent()
    }
}
