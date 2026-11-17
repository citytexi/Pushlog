package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/** [PushlogRadio] 여러 개를 세로로 묶는다. 접근성 도구가 하나만 고르는 한 묶음으로 읽는다. */
@Composable
fun PushlogRadioGroup(
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
private fun PushlogRadioGroupPreview() {
    PushlogPreviewTheme {
        PushlogRadioGroup(Modifier.padding(PushlogTheme.dimension.x4)) {
            var selected by remember { mutableIntStateOf(0) }
            listOf("최신순", "오래된순").forEachIndexed { index, label ->
                PushlogRadio(selected = selected == index, onClick = { selected = index }, label = label)
            }
        }
    }
}
