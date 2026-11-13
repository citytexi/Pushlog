package com.citytexi.pushlog.preview.foundation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** Padded, evenly spaced column shared by the foundation preview bodies. */
@Composable
internal fun PreviewBody(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(PushlogTheme.dimension.x4),
        verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
    ) {
        content()
    }
}
