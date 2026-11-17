package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogPressIndication
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED checkmark.yaml: strokeWidth 1px. 같은 크기의 토큰이 없다.
private val StrokeWidth = 1.dp

/**
 * 체크박스의 체크 표시. 그림만 그리고 탭을 처리하지 않으며 의미 정보도 두지 않는다.
 *
 * 단독으로 쓰면 호출하는 쪽이 줄에 `Modifier.toggleable`을 `indication = null`로 붙이고
 * 같은 [interactionSource]를 넘긴다. 그러면 줄을 누를 때 마크만 눌림 색으로 바뀌고 줄어든다.
 * 감싸는 쪽이 이미 눌림 피드백을 주면 [scaleOnPress]를 false로 둔다.
 */
@Composable
fun PushlogCheckmark(
    state: ToggleableState,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    val on = state != ToggleableState.Off
    val shape = RoundedCornerShape(metrics.cornerRadius)
    val strokeColor = variant.strokeColor(on)
    Box(
        modifier =
            modifier
                .size(metrics.markSize)
                .pushlogPressIndication(
                    interactionSource = source,
                    color = variant.rootColor(tone, on, enabled),
                    pressedColor = variant.rootPressedColor(tone, on),
                    shape = shape,
                    enabled = enabled,
                    scale = scaleOnPress,
                ).then(if (strokeColor != null) Modifier.border(StrokeWidth, strokeColor, shape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        val iconColor = variant.iconColor(tone, on, enabled)
        if (iconColor != null) {
            CheckmarkIcon(state = state, color = iconColor, modifier = Modifier.size(variant.iconSize(metrics)))
        }
    }
}

@Composable
private fun CheckmarkIcon(
    state: ToggleableState,
    color: Color,
    modifier: Modifier,
) {
    val motion = PushlogTheme.motion
    val animated by animateColorAsState(
        targetValue = color,
        animationSpec = tween(motion.duration.colorTransition, easing = motion.timingFunction.easing),
        label = "checkmarkIcon",
    )
    Canvas(modifier) {
        if (state == ToggleableState.Indeterminate) drawDashIcon(animated) else drawCheckIcon(animated)
    }
}

private val PreviewStates = listOf(ToggleableState.Off, ToggleableState.On, ToggleableState.Indeterminate)

@PushlogPreview
@Composable
private fun PushlogCheckmarkPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            PushlogCheckmarkVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                    PreviewStates.forEach { state -> PushlogCheckmark(state = state, variant = variant) }
                }
            }
        }
    }
}
