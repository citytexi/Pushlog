package com.citytexi.pushlog.preview.scaffold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/** Test tag on the root of every detail screen. */
const val PREVIEW_SCAFFOLD_TAG = "preview_scaffold"

/** Shared frame of a detail screen: back button, title, then [content] in a vertical scroll. */
@Composable
fun PreviewScaffold(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxSize().testTag(PREVIEW_SCAFFOLD_TAG)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = PushlogTheme.dimension.x1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Text(text = "뒤로", style = PushlogTheme.typography.t5Medium)
            }
            // No maxLines: at 2× font a long component name wraps instead of being cut off.
            Text(
                text = title,
                modifier = Modifier.weight(1f).padding(horizontal = PushlogTheme.dimension.x2),
                style = PushlogTheme.typography.t6Bold,
                color = PushlogTheme.color.fg.neutral,
            )
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            content()
        }
    }
}

@PushlogPreview
@Composable
private fun PreviewScaffoldPreview() {
    PushlogTheme {
        PreviewScaffold(title = "PushlogButton", onBack = {}) {
            Text(text = "Body", color = PushlogTheme.color.fg.neutral)
        }
    }
}
