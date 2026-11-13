package com.citytexi.pushlog.preview.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** Heading that separates the groups inside a foundation preview body. */
@Composable
internal fun PreviewSectionTitle(text: String) {
    Text(
        text = text,
        style = PushlogTheme.typography.t5Bold,
        color = PushlogTheme.color.fg.neutral,
        modifier = Modifier.padding(top = PushlogTheme.dimension.x3),
    )
}
