package com.citytexi.pushlog.core.designsystem.component.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 체크박스·라디오·스위치가 함께 쓰는 "마크 + 라벨" 한 줄. 탭은 처리하지 않는다.
 * 탭 영역은 호출하는 쪽이 `component.pressable`의 탭 영역 modifier로 [tapArea]에 넘기고,
 * 같은 `MutableInteractionSource`를 마크에도 넘겨 라벨을 눌러도 마크가 줄어들게 한다.
 *
 * [mark]는 위쪽 여백 `(rowMinHeight - markHeight) / 2`가 담긴 modifier를 받는다.
 * [labelAlpha]가 람다인 것은 애니메이션 값 읽기를 그리기 단계로 미뤄 재구성을 피하려는 것이다.
 * null이면 `graphicsLayer`를 붙이지 않는다.
 */
@Composable
internal fun SelectionControlRow(
    tapArea: Modifier,
    rowMinHeight: Dp,
    markHeight: Dp,
    gap: Dp,
    label: String?,
    labelStyle: TextStyle,
    labelColor: Color,
    modifier: Modifier = Modifier,
    labelAlpha: (() -> Float)? = null,
    mark: @Composable (Modifier) -> Unit,
) {
    Row(
        modifier =
            modifier
                .then(tapArea)
                .heightIn(min = rowMinHeight),
        horizontalArrangement = Arrangement.spacedBy(gap),
        verticalAlignment = Alignment.Top,
    ) {
        mark(Modifier.padding(top = (rowMinHeight - markHeight) / 2))
        if (label != null) {
            val lineHeight = with(LocalDensity.current) { labelStyle.lineHeight.toDp() }
            val alphaModifier =
                if (labelAlpha != null) Modifier.graphicsLayer { alpha = labelAlpha() } else Modifier
            Text(
                text = label,
                style = labelStyle,
                color = labelColor,
                modifier =
                    Modifier
                        .weight(1f, fill = false)
                        .padding(top = ((rowMinHeight - lineHeight) / 2).coerceAtLeast(0.dp))
                        .then(alphaModifier),
            )
        }
    }
}
