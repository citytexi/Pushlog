package com.citytexi.pushlog.preview.foundation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

/** Registers the Material 3 screen for [MaterialPreviewKey]. */
fun EntryProviderScope<NavKey>.materialPreviewEntry(onBack: () -> Unit) {
    entry<MaterialPreviewKey> { PreviewScaffold(title = "Material", onBack = onBack) { MaterialPreviewContent() } }
}

/** Stock Material 3 components themed by [PushlogTheme]; draws no background of its own. */
@Composable
fun MaterialPreviewContent() {
    PreviewBody {
        Button(onClick = {}) { Text("Button") }
        FilledTonalButton(onClick = {}) { Text("FilledTonalButton") }
        OutlinedButton(onClick = {}) { Text("OutlinedButton") }
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("Card", modifier = Modifier.padding(PushlogTheme.dimension.x4))
        }
        Snackbar(action = { TextButton(onClick = {}) { Text("Action") } }) { Text("Snackbar") }
        OutlinedTextField(value = "OutlinedTextField", onValueChange = {})
    }
}

@PushlogPreview
@Composable
private fun MaterialPreviewContentPreview() {
    PushlogTheme {
        MaterialPreviewContent()
    }
}
