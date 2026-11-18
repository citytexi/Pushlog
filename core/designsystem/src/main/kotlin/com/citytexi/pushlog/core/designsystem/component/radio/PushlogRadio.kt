package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogSelectableArea
import com.citytexi.pushlog.core.designsystem.component.selection.LocalSelectionGroupEnabled
import com.citytexi.pushlog.core.designsystem.component.selection.SelectionControlRow
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/**
 * 라디오 마크와 라벨을 한 줄로 놓은 라디오 버튼. 줄 전체가 탭 하나라 라벨을 눌러도 [onClick]이 불린다.
 * 눌리면 줄은 그대로고 마크만 눌림 색으로 바뀌고 줄어든다.
 *
 * 같은 질문의 선택지는 [PushlogRadioGroup]으로 묶고, 하나만 고르는 상태는 호출하는 쪽이 갖는다.
 * 마크 위쪽 여백은 `(rowMinHeight - markSize) / 2`로 고정이라 기본 글자 크기에서는 라벨 첫 줄 가운데에 맞고,
 * 라벨이 여러 줄이어도 첫 줄 옆에 남는다. 글자가 커져 첫 줄이 `rowMinHeight`보다 높으면 첫 줄 가운데보다 조금 위에 놓인다.
 */
@Composable
fun PushlogRadio(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    tone: PushlogRadiomarkTone = PushlogRadiomarkTone.NEUTRAL,
    size: PushlogRadiomarkSize = PushlogRadiomarkSize.MEDIUM,
    weight: PushlogRadioWeight = PushlogRadioWeight.REGULAR,
    enabled: Boolean = LocalSelectionGroupEnabled.current,
    interactionSource: MutableInteractionSource? = null,
) {
    // 줄의 탭 영역과 마크가 같은 소스를 봐야 라벨을 눌러도 마크가 줄어든다.
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val metrics = size.metrics()
    SelectionControlRow(
        tapArea = Modifier.pushlogSelectableArea(selected, source, enabled, onClick),
        rowMinHeight = metrics.rowMinHeight,
        markHeight = metrics.markSize,
        gap = PushlogTheme.dimension.x2,
        label = label,
        labelStyle = weight.labelStyle(metrics),
        labelColor = if (enabled) PushlogTheme.color.fg.neutral else PushlogTheme.color.fg.disabled,
        modifier = modifier,
    ) { markModifier ->
        PushlogRadiomark(
            selected = selected,
            modifier = markModifier,
            tone = tone,
            size = size,
            enabled = enabled,
            interactionSource = source,
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogRadioPreview() {
    PushlogPreviewTheme {
        PushlogRadioGroup(Modifier.padding(PushlogTheme.dimension.x4)) {
            var selected by remember { mutableIntStateOf(0) }
            listOf("선택됨", "선택 안 됨").forEachIndexed { index, label ->
                PushlogRadio(selected = selected == index, onClick = { selected = index }, label = label)
            }
            PushlogRadio(selected = true, onClick = {}, label = "비활성", enabled = false)
        }
    }
}
