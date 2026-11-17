package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogTriStateToggleArea
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/**
 * 체크 마크와 라벨을 한 줄로 놓은 체크박스. 줄 전체가 탭 하나라 라벨을 눌러도 [onClick]이 불린다.
 * 눌리면 줄은 그대로고 마크만 눌림 색으로 바뀌고 줄어든다.
 *
 * 하위 항목 일부만 고른 부모의 [ToggleableState.Indeterminate]는 호출하는 쪽이 자식 상태로 계산해 넘긴다.
 * 마크 위쪽 여백은 `(rowMinHeight - markSize) / 2`로 고정이라 기본 글자 크기에서는 라벨 첫 줄 가운데에 맞고,
 * 라벨이 여러 줄이어도 첫 줄 옆에 남는다. 글자가 커져 첫 줄이 `rowMinHeight`보다 높으면 첫 줄 가운데보다 조금 위에 놓인다.
 */
@Composable
fun PushlogCheckbox(
    state: ToggleableState,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    weight: PushlogCheckboxWeight = PushlogCheckboxWeight.REGULAR,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    // 줄의 탭 영역과 마크가 같은 소스를 봐야 라벨을 눌러도 마크가 줄어든다.
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    val labelStyle = weight.labelStyle(metrics)
    val lineHeight = with(LocalDensity.current) { labelStyle.lineHeight.toDp() }
    Row(
        modifier =
            modifier
                .pushlogTriStateToggleArea(state, source, enabled, onClick)
                .heightIn(min = metrics.rowMinHeight),
        horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        verticalAlignment = Alignment.Top,
    ) {
        PushlogCheckmark(
            state = state,
            modifier = Modifier.padding(top = (metrics.rowMinHeight - metrics.markSize) / 2),
            variant = variant,
            tone = tone,
            size = size,
            enabled = enabled,
            interactionSource = source,
        )
        Text(
            text = label,
            style = labelStyle,
            color = if (enabled) PushlogTheme.color.fg.neutral else PushlogTheme.color.fg.disabled,
            modifier =
                Modifier
                    .weight(1f, fill = false)
                    .padding(top = ((metrics.rowMinHeight - lineHeight) / 2).coerceAtLeast(0.dp)),
        )
    }
}

/** [checked]를 [ToggleableState]로 바꿔 부르는 편의 오버로드. 탭하면 [onCheckedChange]에 반대 값을 넘긴다. */
@Composable
fun PushlogCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    variant: PushlogCheckmarkVariant = PushlogCheckmarkVariant.SQUARE,
    tone: PushlogCheckmarkTone = PushlogCheckmarkTone.NEUTRAL,
    size: PushlogCheckmarkSize = PushlogCheckmarkSize.MEDIUM,
    weight: PushlogCheckboxWeight = PushlogCheckboxWeight.REGULAR,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    PushlogCheckbox(
        state = ToggleableState(checked),
        onClick = { onCheckedChange(!checked) },
        label = label,
        modifier = modifier,
        variant = variant,
        tone = tone,
        size = size,
        weight = weight,
        enabled = enabled,
        interactionSource = interactionSource,
    )
}

@PushlogPreview
@Composable
private fun PushlogCheckboxPreview() {
    PushlogPreviewTheme {
        Column(Modifier.padding(PushlogTheme.dimension.x4)) {
            PushlogCheckbox(state = ToggleableState.On, onClick = {}, label = "선택됨")
            PushlogCheckbox(state = ToggleableState.Indeterminate, onClick = {}, label = "일부 선택")
            PushlogCheckbox(state = ToggleableState.Off, onClick = {}, label = "선택 안 됨")
            PushlogCheckbox(state = ToggleableState.On, onClick = {}, label = "비활성", enabled = false)
        }
    }
}
