package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED chip.yaml: SMALL 최소 너비. 같은 크기의 간격 토큰이 없다.
private val SmallMinWidth = 44.dp

// SEED chip.yaml: OUTLINE_WEAK 테두리 두께.
private val BorderWidth = 1.dp

private const val DISABLED_ALPHA = 0.5f

private class ChipMetrics(
    val minHeight: Dp,
    val paddingX: Dp,
    val prefixIconSize: Dp,
    val suffixIconSize: Dp,
    val minWidth: Dp,
)

/**
 * 필터·선택에 쓰는 알약 모양 칩. [selected]가 null이면 버튼이고, 아니면 켜고 끄는 체크 상태를 가진다.

 * [onClick]은 탭할 때 불리고, [selected]를 뒤집는 일은 호출하는 쪽이 맡는다.
 * 뒤 아이콘은 그림일 뿐이고 칩 전체가 탭 하나다. 라벨은 한 줄이며 넘치면 말줄임이 된다.
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
    val metrics = chipMetrics(size)
    val labelPadding = PushlogTheme.dimension.x1Half
    val shape = PushlogTheme.radius.full
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val color = surfaceColor(variant, isSelected, pressed = false)
    val pressedColor = surfaceColor(variant, isSelected, pressed = true)
    val contentColor = contentColor(variant, isSelected)
    val borderColor =
        when {
            variant == PushlogChipVariant.SOLID -> Color.Transparent
            isSelected -> PushlogTheme.color.stroke.neutralContrast
            else -> PushlogTheme.color.stroke.neutralWeak
        }
    val surface =
        if (selected == null) {
            Modifier.pushlogPressable(source, color, pressedColor, shape, enabled, loading = false, onClick = onClick)
        } else {
            Modifier.pushlogToggleablePressable(source, selected, color, pressedColor, shape, enabled, loading = false, onClick = onClick)
        }
    Row(
        modifier =
            modifier
                .alpha(if (enabled) 1f else DISABLED_ALPHA)
                .then(surface)
                .then(if (variant == PushlogChipVariant.OUTLINE_WEAK) Modifier.border(BorderWidth, borderColor, shape) else Modifier)
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

@Composable
private fun chipMetrics(size: PushlogChipSize): ChipMetrics {
    val dimension = PushlogTheme.dimension
    return when (size) {
        PushlogChipSize.SMALL -> ChipMetrics(dimension.x8, dimension.x1Half, dimension.x3Half, dimension.x3Half, SmallMinWidth)
        PushlogChipSize.MEDIUM -> ChipMetrics(dimension.x9, dimension.x2, dimension.x4, dimension.x3Half, dimension.x12)
        PushlogChipSize.LARGE -> ChipMetrics(dimension.x10, dimension.x2Half, dimension.x4, dimension.x4, dimension.x13)
    }
}

@Composable
private fun surfaceColor(
    variant: PushlogChipVariant,
    selected: Boolean,
    pressed: Boolean,
): Color {
    val bg = PushlogTheme.color.bg
    return when (variant) {
        PushlogChipVariant.SOLID ->
            when {
                selected -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
                else -> if (pressed) bg.neutralWeakAlphaPressed else bg.neutralWeakAlpha
            }
        PushlogChipVariant.OUTLINE_WEAK ->
            when {
                selected -> if (pressed) bg.neutralWeakPressed else bg.neutralWeak
                else -> if (pressed) bg.transparentPressed else bg.transparent
            }
    }
}

@Composable
private fun contentColor(
    variant: PushlogChipVariant,
    selected: Boolean,
): Color {
    val fg = PushlogTheme.color.fg
    return if (variant == PushlogChipVariant.SOLID && selected) fg.onNeutralSolid else fg.neutral
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
