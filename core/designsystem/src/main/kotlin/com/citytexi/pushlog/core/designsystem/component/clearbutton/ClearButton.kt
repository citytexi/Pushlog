package com.citytexi.pushlog.core.designsystem.component.clearbutton

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface

/** 투명 표면에 지우기 아이콘을 그린다. 눌리면 자기만 줄어든다(SEED clearButton scaleScope: self). */
@Composable
internal fun ClearButton(
    onClick: () -> Unit,
    contentDescription: String,
    iconSize: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    touchPadding: Dp = 0.dp,
) {
    PushlogPressableSurface(
        color = Color.Transparent,
        pressedColor = Color.Transparent,
        shape = CircleShape,
        enabled = true,
        onClick = onClick,
        modifier = modifier.semantics { this.contentDescription = contentDescription },
    ) {
        Box(Modifier.padding(touchPadding)) {
            Canvas(Modifier.size(iconSize)) { drawXmarkCircleIcon(color) }
        }
    }
}
