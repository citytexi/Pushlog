package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/** 입력 위에 놓는 라벨 글자. 글자 입력 자체는 라벨을 그리지 않는다. */
@Composable
fun PushlogFieldLabel(
    text: String,
    modifier: Modifier = Modifier,
    weight: PushlogFieldLabelWeight = PushlogFieldLabelWeight.MEDIUM,
) {
    Text(text = text, modifier = modifier, style = weight.textStyle, color = PushlogTheme.color.fg.neutral)
}

@PushlogPreview
@Composable
private fun PushlogFieldLabelPreview() {
    PushlogPreviewTheme {
        Column {
            PushlogFieldLabelWeight.entries.forEach { PushlogFieldLabel(text = it.name, weight = it) }
        }
    }
}
