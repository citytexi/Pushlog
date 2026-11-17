package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.component.pressable.PressScaleScope
import com.citytexi.pushlog.core.designsystem.component.pressable.PressableSelectionMode
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

// SEED segmented-control-item.yaml: minHeight 34px. 같은 크기의 토큰이 없다.
private val ItemMinHeight = 34.dp

// SEED segmented-control-item.yaml: 눌림·비활성 strokeWidth 1px(indicator와 같다). 같은 크기의 토큰이 없다.
private val StrokeWidth = 1.dp

/**
 * 세그먼트 칸 하나. 칸 크기는 [PushlogSegmentedControl]이 고정 제약으로 정하므로 표면이 칸을 다 채운다.
 * 라벨은 줄 수 제한 없이 가운데 정렬로 줄바꿈된다.
 *
 * indicator 색은 바뀌지 않는다. 선택 항목이 눌리면 gray100, 비활성이면 `bg.disabled` 배경과 indicator와 같은 1dp 테두리를
 * 항목이 직접 칠해 indicator를 덮는다(SEED recipe). 그래서 indicator가 움직이는 동안에도 덮개는 이 칸에 머문다.
 * 눌리면 표면은 그대로 두고 라벨만 준다.
 */
@Composable
internal fun SegmentedControlItem(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = PushlogTheme.color
    val motion = PushlogTheme.motion
    val labelColor =
        animateColorAsState(
            targetValue =
                when {
                    !enabled -> theme.fg.disabled
                    selected -> theme.fg.neutral
                    else -> theme.fg.neutralSubtle
                },
            animationSpec = tween(motion.duration.colorTransition, easing = motion.timingFunction.easing),
            label = "segmentedControlLabel",
        )
    val pressed = if (selected) pushlogThemedColor(LightPalette.gray100, DarkPalette.gray100) else theme.bg.neutralWeakPressed
    val coversDisabled = selected && !enabled
    val stroke = BorderStroke(StrokeWidth, theme.stroke.neutralMuted)
    PushlogPressableSurface(
        // 투명은 눌림 색과 같은 색조의 alpha 0으로 둔다. Color.Transparent(검정 alpha 0)에서 번지면 중간에 어두운 회색이 지나간다.
        // 비활성 선택 배경으로 바뀔 때도 표면의 색 전환(colorTransition)을 탄다.
        color = if (coversDisabled) theme.bg.disabled else pressed.copy(alpha = 0f),
        pressedColor = pressed,
        shape = PushlogTheme.radius.full,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
        selected = selected,
        selectionMode = PressableSelectionMode.SINGLE,
        scaleScope = PressScaleScope.CONTENT,
        border = if (coversDisabled) stroke else null,
        pressedBorder = stroke,
        interactionSource = interactionSource,
    ) {
        Box(
            Modifier
                .heightIn(min = ItemMinHeight)
                .padding(horizontal = PushlogTheme.dimension.x6, vertical = PushlogTheme.dimension.x1Half),
            contentAlignment = Alignment.Center,
        ) {
            // 색을 그리기 단계에서 읽어 색이 바뀌는 동안 다시 구성하지 않는다.
            BasicText(
                text = label,
                style = PushlogTheme.typography.t5Bold.copy(textAlign = TextAlign.Center),
                color = { labelColor.value },
            )
        }
    }
}
