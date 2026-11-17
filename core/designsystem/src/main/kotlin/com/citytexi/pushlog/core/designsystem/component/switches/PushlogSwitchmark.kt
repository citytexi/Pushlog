package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogPressScale
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED switchmark.yaml colorDelay 20ms. 같은 길이의 토큰이 없다.
private const val COLOR_DELAY_MILLIS = 20

// SEED switchmark.yaml: 꺼진 thumb은 0.8배. 같은 값의 토큰이 없다.
private const val OFF_THUMB_SCALE = 0.8f

// SEED switchmark.yaml disabled opacity 0.38.
private const val DISABLED_ALPHA = 0.38f

/**
 * 스위치의 트랙과 thumb. 그림만 그리고 탭을 처리하지 않으며 의미 정보도 두지 않는다.
 * 눌리면 마크가 줄어들기만 하고 색은 바뀌지 않는다(SEED switchmark).
 *
 * 단독으로 쓰면 호출하는 쪽이 줄에 `Modifier.toggleable`을 `indication = null`, `role = Role.Switch`로 붙이고
 * 같은 [interactionSource]를 넘긴다. 감싸는 쪽이 이미 눌림 피드백을 주면 [scaleOnPress]를 false로 둔다.
 * thumb 이동·크기는 SEED recipe처럼 한 시간(150ms)에 묶는다. 이동 도중 [checked]가 바뀌면 지금 자리에서 돌아온다.
 */
@Composable
fun PushlogSwitchmark(
    checked: Boolean,
    modifier: Modifier = Modifier,
    tone: PushlogSwitchmarkTone = PushlogSwitchmarkTone.NEUTRAL,
    size: PushlogSwitchmarkSize = PushlogSwitchmarkSize.LARGE,
    enabled: Boolean = true,
    scaleOnPress: Boolean = true,
    interactionSource: InteractionSource? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    val motion = PushlogTheme.motion
    val easing = motion.timingFunction.easing
    val shape = PushlogTheme.radius.full
    val fraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(motion.duration.d3, easing = easing),
        label = "switchmarkThumb",
    )
    val colorSpec = tween<Color>(motion.duration.d1, COLOR_DELAY_MILLIS, easing)
    val trackColor by animateColorAsState(tone.trackColor(checked, enabled), colorSpec, label = "switchmarkTrack")
    val thumbColor by animateColorAsState(tone.thumbColor(checked, enabled), colorSpec, label = "switchmarkThumbColor")
    val disabledAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else DISABLED_ALPHA,
        animationSpec = tween(motion.duration.d1, easing = easing),
        label = "switchmarkAlpha",
    )
    val travel = metrics.trackWidth - metrics.trackHeight
    Box(
        modifier =
            modifier
                .size(metrics.trackWidth, metrics.trackHeight)
                .then(if (scaleOnPress) Modifier.pushlogPressScale(source, enabled) else Modifier)
                .graphicsLayer { alpha = disabledAlpha }
                .background(trackColor, shape)
                .padding(metrics.padding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .size(metrics.thumbSize)
                // fraction을 층 블록 안에서 읽어 애니메이션 중에는 다시 그리기만 한다.
                .graphicsLayer {
                    val scale = lerp(OFF_THUMB_SCALE, 1f, fraction)
                    translationX = travel.toPx() * fraction
                    scaleX = scale
                    scaleY = scale
                }.background(thumbColor, shape),
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchmarkPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            PushlogSwitchmarkTone.entries.forEach { tone ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                    PushlogSwitchmark(checked = false, tone = tone)
                    PushlogSwitchmark(checked = true, tone = tone)
                    PushlogSwitchmark(checked = false, tone = tone, enabled = false)
                    PushlogSwitchmark(checked = true, tone = tone, enabled = false)
                }
            }
        }
    }
}
