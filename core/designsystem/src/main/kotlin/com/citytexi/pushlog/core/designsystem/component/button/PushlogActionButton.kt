package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/**
 * 글자 버튼. 기본 [PushlogActionButtonVariant.NEUTRAL_WEAK]는 SEED가 "대부분의 액션"으로 꼽은 강조다.
 * 강조 Solid는 화면당 하나만 쓴다. [loading] 중에는 너비를 유지한 채 클릭만 막힌다.
 */
@Composable
fun PushlogActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PushlogActionButtonVariant = PushlogActionButtonVariant.NEUTRAL_WEAK,
    size: PushlogActionButtonSize = PushlogActionButtonSize.MEDIUM,
    prefixIcon: Painter? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val contentColor = variant.contentColor(enabled)
    val metrics = size.metrics()
    ActionButtonSurface(
        variant = variant,
        size = size,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
        interactionSource = interactionSource,
        contentColor = contentColor,
        minWidth = 0.dp,
        modifier = modifier,
        semanticsModifier = Modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = metrics.paddingX, vertical = metrics.paddingY),
            horizontalArrangement = Arrangement.spacedBy(metrics.gap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefixIcon != null) {
                Icon(painter = prefixIcon, contentDescription = null, modifier = Modifier.size(metrics.prefixIconSize), tint = contentColor)
            }
            Text(text = text, style = metrics.labelStyle, color = contentColor)
        }
    }
}

/** 아이콘만 있는 버튼. [contentDescription]이 접근성 이름이다. */
@Composable
fun PushlogActionButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PushlogActionButtonVariant = PushlogActionButtonVariant.NEUTRAL_WEAK,
    size: PushlogActionButtonSize = PushlogActionButtonSize.MEDIUM,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val contentColor = variant.contentColor(enabled)
    val metrics = size.metrics()
    ActionButtonSurface(
        variant = variant,
        size = size,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
        interactionSource = interactionSource,
        contentColor = contentColor,
        minWidth = metrics.minHeight,
        modifier = modifier,
        semanticsModifier = Modifier.semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding(metrics.iconOnlyPadding).size(metrics.iconOnlyIconSize),
            tint = contentColor,
        )
    }
}

@Composable
private fun ActionButtonSurface(
    variant: PushlogActionButtonVariant,
    size: PushlogActionButtonSize,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource?,
    contentColor: Color,
    minWidth: Dp,
    modifier: Modifier,
    semanticsModifier: Modifier,
    content: @Composable () -> Unit,
) {
    val metrics = size.metrics()
    val shape = remember(metrics.cornerRadius) { RoundedCornerShape(metrics.cornerRadius) }
    val disabledSurface = variant.disabledSurfaceColor()
    val pressedColor = buttonPressedColor(enabled, variant.surfaceColor(pressed = true), disabledSurface)
    val color = buttonSurfaceColor(enabled, loading, variant.surfaceColor(pressed = false), pressedColor, disabledSurface)
    PushlogPressableSurface(
        color = color,
        pressedColor = pressedColor,
        shape = shape,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.defaultMinSize(minWidth = minWidth, minHeight = metrics.minHeight).then(semanticsModifier),
        loading = loading,
        interactionSource = interactionSource,
    ) {
        PushlogLoadingContent(loading, metrics.progressSize, variant.trackColor(), contentColor, content)
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonPreview() {
    PushlogPreviewTheme {
        Column(Modifier.padding(PushlogTheme.dimension.x4), verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
            val plus = rememberVectorPainter(PreviewSampleIcons.Plus)
            PushlogActionButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
                    PushlogActionButton(text = "확인", onClick = {}, variant = variant, prefixIcon = plus)
                    PushlogActionButton(text = "확인", onClick = {}, variant = variant, enabled = false)
                    PushlogActionButton(text = "확인", onClick = {}, variant = variant, loading = true)
                    PushlogActionButton(icon = plus, contentDescription = "추가", onClick = {}, variant = variant)
                }
            }
        }
    }
}
