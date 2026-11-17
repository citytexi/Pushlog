package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogToggleableArea
import com.citytexi.pushlog.core.designsystem.component.selection.SelectionControlRow
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED switch.yaml disabled label opacity
private const val LABEL_DISABLED_ALPHA = 0.58f

/**
 * 스위치 마크와 라벨을 한 줄로 놓은 스위치. 바로 반영되는 설정에만 쓴다. 저장을 눌러야 반영되면 체크박스를 쓴다.
 * 줄 전체가 탭 하나라 라벨을 눌러도 [onCheckedChange]가 불리고, 눌리면 줄은 그대로고 마크만 줄어든다.
 *
 * [label]은 [showLabel]이 false여도 줄의 접근성 이름으로 남는다. 이름 없는 스위치를 컴파일 단계에서 막으려고 필수로 받는다.
 * 비활성 라벨은 색을 바꾸지 않고 투명도로만 흐려진다. 마크는 라벨 첫 줄 옆에 붙는다.
 */
@Composable
fun PushlogSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    tone: PushlogSwitchmarkTone = PushlogSwitchmarkTone.NEUTRAL,
    size: PushlogSwitchmarkSize = PushlogSwitchmarkSize.LARGE,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    // 줄의 탭 영역과 마크가 같은 소스를 봐야 라벨을 눌러도 마크가 줄어든다.
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    val motion = PushlogTheme.motion
    val labelAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else LABEL_DISABLED_ALPHA,
        animationSpec = tween(motion.duration.d1, easing = motion.timingFunction.easing),
        label = "switchLabelAlpha",
    )
    SelectionControlRow(
        tapArea = Modifier.pushlogToggleableArea(checked, source, enabled, Role.Switch, onCheckedChange),
        rowMinHeight = metrics.rowMinHeight,
        markHeight = metrics.trackHeight,
        gap = metrics.gap,
        label = if (showLabel) label else null,
        labelStyle = metrics.labelStyle,
        labelColor = PushlogTheme.color.fg.neutral,
        modifier = modifier.then(if (!showLabel) Modifier.semantics { contentDescription = label } else Modifier),
        labelAlpha = { labelAlpha },
    ) { markModifier ->
        PushlogSwitchmark(
            checked = checked,
            modifier = markModifier,
            tone = tone,
            size = size,
            enabled = enabled,
            scaleOnPress = true,
            interactionSource = source,
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogSwitchPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            var checked by remember { mutableStateOf(true) }
            PushlogSwitch(checked = checked, onCheckedChange = { checked = it }, label = "켜짐")
            PushlogSwitch(checked = false, onCheckedChange = {}, label = "꺼짐")
            PushlogSwitch(checked = true, onCheckedChange = {}, label = "비활성", enabled = false)
            PushlogSwitch(checked = true, onCheckedChange = {}, label = "라벨 숨김", showLabel = false)
        }
    }
}
