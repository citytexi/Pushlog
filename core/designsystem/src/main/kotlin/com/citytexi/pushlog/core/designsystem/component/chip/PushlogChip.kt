package com.citytexi.pushlog.core.designsystem.component.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED chip.yaml: OUTLINE_WEAK 테두리 두께.
private val BorderWidth = 1.dp

private const val DISABLED_ALPHA = 0.5f

/**
 * 필터·선택에 쓰는 알약 모양 칩. [selected]가 null이면 버튼이고, 아니면 켜고 끄는 체크 상태를 가진다.
 *
 * [onClick]은 탭할 때 불리고, [selected]를 뒤집는 일은 호출하는 쪽이 맡는다.
 * 뒤 아이콘은 그림일 뿐이고 칩 전체가 탭 하나다.
 */
@Composable
fun PushlogChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PushlogChipVariant = PushlogChipVariant.OUTLINE_WEAK,
    size: PushlogChipSize = PushlogChipSize.MEDIUM,
    selected: Boolean? = null,
    prefixIcon: Painter? = null,
    suffixIcon: Painter? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val isSelected = selected == true
    val metrics = size.metrics()
    val labelPadding = PushlogTheme.dimension.x1Half
    val shape = PushlogTheme.radius.full
    val color = variant.surfaceColor(isSelected, pressed = false)
    val pressedColor = variant.surfaceColor(isSelected, pressed = true)
    val contentColor = variant.contentColor(isSelected)
    val borderColor = variant.borderColor(isSelected)
    PushlogPressableSurface(
        color = color,
        pressedColor = pressedColor,
        shape = shape,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.alpha(if (enabled) 1f else DISABLED_ALPHA),
        selected = selected,
        border = if (variant == PushlogChipVariant.OUTLINE_WEAK) BorderStroke(BorderWidth, borderColor) else null,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier =
                Modifier
                    .widthIn(min = metrics.minWidth)
                    .heightIn(min = metrics.minHeight)
                    .padding(horizontal = metrics.paddingX),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefixIcon != null) {
                Icon(
                    painter = prefixIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(start = labelPadding).size(metrics.prefixIconSize),
                    tint = contentColor,
                )
            }
            Text(
                text = text,
                style = PushlogTheme.typography.t4Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false).padding(horizontal = labelPadding),
            )
            if (suffixIcon != null) {
                Icon(
                    painter = suffixIcon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = labelPadding).size(metrics.suffixIconSize),
                    tint = contentColor,
                )
            }
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogChipPreview() {
    PushlogTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
            val down = rememberVectorPainter(PreviewSampleIcons.ChevronDown)
            PushlogChipVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                    PushlogChip(text = "전체", onClick = {}, variant = variant, prefixIcon = plus, suffixIcon = down)
                    PushlogChip(text = "선택", onClick = {}, variant = variant, selected = true)
                    PushlogChip(text = "비활성", onClick = {}, variant = variant, enabled = false)
                }
            }
        }
    }
}
