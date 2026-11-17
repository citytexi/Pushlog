package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/** [PushlogCheckbox] 여러 개를 세로로 묶는다. 접근성 도구가 한 묶음으로 읽는다. */
@Composable
fun PushlogCheckboxGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x1),
        content = content,
    )
}

@PushlogPreview
@Composable
private fun PushlogCheckboxGroupPreview() {
    PushlogTheme {
        PushlogCheckboxGroup(Modifier.padding(PushlogTheme.dimension.x4)) {
            var first by remember { mutableStateOf(true) }
            var second by remember { mutableStateOf(false) }
            PushlogCheckbox(checked = first, onCheckedChange = { first = it }, label = "메신저")
            PushlogCheckbox(checked = second, onCheckedChange = { second = it }, label = "쇼핑")
        }
    }
}
