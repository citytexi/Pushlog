package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogPressIndication
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED radiomark.yaml: strokeWidth 1px. 같은 크기의 토큰이 없다.
private val StrokeWidth = 1.dp

/**
 * 라디오 버튼의 동그라미 표시. 그림만 그리고 탭을 처리하지 않으며 의미 정보도 두지 않는다.
 *
 * 단독으로 쓰면 호출하는 쪽이 줄에 `Modifier.selectable`을 `indication = null`로 붙이고
 * 같은 [interactionSource]를 넘긴다. 그러면 줄을 누를 때 마크만 눌림 색으로 바뀌고 줄어든다.
 * 감싸는 쪽이 이미 눌림 피드백을 주면 [scaleOnPress]를 false로 둔다.
 */
@Composable
fun PushlogRadiomark(
    selected: Boolean,
    modifier: Modifier = Modifier,
    tone: PushlogRadiomarkTone = PushlogRadiomarkTone.NEUTRAL,
    size: PushlogRadiomarkSize = PushlogRadiomarkSize.MEDIUM,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    val shape = PushlogTheme.radius.full
    val strokeColor = tone.strokeColor(selected, enabled)
    Box(
        modifier =
            modifier
                .size(metrics.markSize)
                .pushlogPressIndication(
                    interactionSource = source,
                    color = tone.rootColor(selected, enabled),
                    pressedColor = tone.rootPressedColor(selected),
                    shape = shape,
                    enabled = enabled,
                    scale = scaleOnPress,
                ).then(if (strokeColor != null) Modifier.border(StrokeWidth, strokeColor, shape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            val dotSize = if (enabled) metrics.dotSize else metrics.disabledDotSize
            Box(Modifier.size(dotSize).background(tone.dotColor(enabled), shape))
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogRadiomarkPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            PushlogRadiomarkTone.entries.forEach { tone ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                    PushlogRadiomark(selected = false, tone = tone)
                    PushlogRadiomark(selected = true, tone = tone)
                    PushlogRadiomark(selected = false, tone = tone, enabled = false)
                    PushlogRadiomark(selected = true, tone = tone, enabled = false)
                }
            }
        }
    }
}
