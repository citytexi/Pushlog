package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.pushlogFieldSemantics
import com.citytexi.pushlog.core.designsystem.component.selection.LocalSelectionGroupEnabled
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/** [PushlogCheckbox] 여러 개를 세로로 묶는다. 접근성 도구가 한 묶음으로 읽는다. */
@Composable
fun PushlogCheckboxGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val field = LocalPushlogFieldState.current
    Column(
        modifier = modifier.selectableGroup().pushlogFieldSemantics(field, field.invalid),
        verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x1),
    ) {
        CompositionLocalProvider(LocalSelectionGroupEnabled provides field.enabled) {
            ResetPushlogFieldState { content() }
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogCheckboxGroupPreview() {
    PushlogPreviewTheme {
        PushlogCheckboxGroup(Modifier.padding(PushlogTheme.dimension.x4)) {
            var first by remember { mutableStateOf(true) }
            var second by remember { mutableStateOf(false) }
            PushlogCheckbox(checked = first, onCheckedChange = { first = it }, label = "메신저")
            PushlogCheckbox(checked = second, onCheckedChange = { second = it }, label = "쇼핑")
        }
    }
}
