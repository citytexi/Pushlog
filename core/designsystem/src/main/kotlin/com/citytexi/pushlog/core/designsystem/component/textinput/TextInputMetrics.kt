package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

/** 값이 같으면 같은 객체로 보아 키 입력마다 상자를 다시 그리지 않게 한다. */
internal data class TextInputMetrics(
    val minHeight: Dp,
    val paddingX: Dp,
    val paddingY: Dp,
    val gap: Dp,
    val cornerRadius: Dp,
    val textStyle: TextStyle,
    val iconSize: Dp,
)
