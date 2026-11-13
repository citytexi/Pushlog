package com.citytexi.pushlog.preview.foundation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import com.citytexi.pushlog.preview.scaffold.PreviewScaffold

private const val SAMPLE_TEXT = "알림 보관함 Aa 123"

/** Registers the typography screen for [TypographyPreviewKey]. */
fun EntryProviderScope<NavKey>.typographyPreviewEntry(onBack: () -> Unit) {
    entry<TypographyPreviewKey> {
        PreviewScaffold(title = "Typography", onBack = onBack) {
            TypographyPreviewContent(styles = allTypographyStyles())
        }
    }
}

/** Each of [styles] rendered with a sample sentence under its name; draws no background of its own. */
@Composable
fun TypographyPreviewContent(styles: List<Pair<String, TextStyle>>) {
    PreviewBody {
        styles.forEach { (name, style) ->
            Text(name, style = PushlogTheme.typography.t2Regular, color = PushlogTheme.color.fg.neutralMuted)
            Text(SAMPLE_TEXT, style = style, color = PushlogTheme.color.fg.neutral)
        }
    }
}

@PushlogPreview
@Composable
private fun TypographyPreviewContentPreview() {
    PushlogTheme {
        TypographyPreviewContent(styles = allTypographyStyles())
    }
}
