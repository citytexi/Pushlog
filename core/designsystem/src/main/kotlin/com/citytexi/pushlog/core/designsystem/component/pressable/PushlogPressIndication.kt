package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape

/** 탭을 받지 않고 [interactionSource]가 눌리면 뒤따르는 내용만 줄인다. */
internal fun Modifier.pushlogPressScale(
    interactionSource: InteractionSource,
    enabled: Boolean,
): Modifier = then(PressScaleNode.Element(interactionSource, enabled, loading = false))

/**
 * 탭을 받지 않는 눌림 표시. 눌리면 배경이 [pressedColor]로 바뀌고 [scale]이면 줄어든다.
 * 이 뒤에 붙인 테두리와 내용도 축소 층 안이라 함께 준다.
 * 탭 영역은 이 modifier 바깥(줄)에 두고 같은 [interactionSource]를 넘긴다.
 */
internal fun Modifier.pushlogPressIndication(
    interactionSource: InteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    scale: Boolean = true,
): Modifier =
    (if (scale) pushlogPressScale(interactionSource, enabled) else this)
        .clip(shape)
        .then(PressColorNode.Element(interactionSource, color, pressedColor, enabled, loading = false))
