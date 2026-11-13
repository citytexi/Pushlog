package com.citytexi.pushlog.preview.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.component.LoadingContent
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.foundation.PreviewBody
import com.citytexi.pushlog.preview.foundation.PreviewSectionTitle
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private val SAMPLE_HEIGHT = 96.dp

/** Registers the LoadingContent screen for [LoadingContentPreviewKey]. */
fun EntryProviderScope<NavKey>.loadingContentPreviewEntry(onBack: () -> Unit) {
    entry<LoadingContentPreviewKey> { PreviewScaffold(title = "LoadingContent", onBack = onBack) { LoadingContentPreviewContent() } }
}

/** LoadingContent in its first-load, refresh and done states; draws no background of its own. */
@Composable
fun LoadingContentPreviewContent() {
    PreviewBody {
        PreviewSectionTitle("first load (isLoading, no content)")
        Sample(isLoading = true, hasContent = false)
        PreviewSectionTitle("refresh (isLoading, has content)")
        Sample(isLoading = true, hasContent = true)
        PreviewSectionTitle("done (content only)")
        Sample(isLoading = false, hasContent = true)
    }
}

@Composable
private fun Sample(
    isLoading: Boolean,
    hasContent: Boolean,
) {
    LoadingContent(
        isLoading = isLoading,
        hasContent = hasContent,
        modifier = Modifier.fillMaxWidth().height(SAMPLE_HEIGHT),
    ) {
        Text("Content", style = PushlogTheme.typography.t4Medium, color = PushlogTheme.color.fg.neutral)
    }
}

@PushlogPreview
@Composable
private fun LoadingContentPreviewContentPreview() {
    PushlogTheme {
        LoadingContentPreviewContent()
    }
}
