package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.shadow.pushlogShadow
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED contextual-floating-button.yaml: 아이콘만 있는 버튼의 크기. 같은 크기의 간격 토큰이 없다.
private val IconOnlySize = 44.dp

// SEED contextual-floating-button.yaml: 아이콘만 있는 버튼의 아이콘 크기. 같은 크기의 간격 토큰이 없다.
private val IconOnlyIconSize = 22.dp

/** 스크롤 목록 위에 떠서 맥락에 맞는 액션을 주는 글자 버튼. [loading] 중에는 너비를 유지한 채 클릭만 막힌다. */
@Composable
fun PushlogContextualFloatingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PushlogContextualFloatingButtonVariant = PushlogContextualFloatingButtonVariant.SOLID,
    prefixIcon: Painter? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val contentColor = variant.contentColor(enabled)
    val dimension = PushlogTheme.dimension
    FloatingButtonSurface(
        variant = variant,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
        interactionSource = interactionSource,
        contentColor = contentColor,
        modifier = modifier.defaultMinSize(minHeight = dimension.x9),
        semanticsModifier = Modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = dimension.x3Half, vertical = dimension.x2),
            horizontalArrangement = Arrangement.spacedBy(dimension.x1, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefixIcon != null) {
                Icon(painter = prefixIcon, contentDescription = null, modifier = Modifier.size(dimension.x4), tint = contentColor)
            }
            Text(
                text = text,
                style = PushlogTheme.typography.t4Medium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

/** 아이콘만 있는 플로팅 버튼. 항상 [PushlogContextualFloatingButtonVariant.LAYER]이고 [contentDescription]이 접근성 이름이다. */
@Composable
fun PushlogContextualFloatingButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val variant = PushlogContextualFloatingButtonVariant.LAYER
    val contentColor = variant.contentColor(enabled)
    FloatingButtonSurface(
        variant = variant,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
        interactionSource = interactionSource,
        contentColor = contentColor,
        modifier = modifier.defaultMinSize(minWidth = IconOnlySize, minHeight = IconOnlySize),
        semanticsModifier = Modifier.semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.padding((IconOnlySize - IconOnlyIconSize) / 2).size(IconOnlyIconSize),
            tint = contentColor,
        )
    }
}

@Composable
private fun FloatingButtonSurface(
    variant: PushlogContextualFloatingButtonVariant,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource?,
    contentColor: Color,
    modifier: Modifier,
    semanticsModifier: Modifier,
    content: @Composable () -> Unit,
) {
    val disabled = PushlogTheme.color.bg.disabled
    val shape = PushlogTheme.radius.full
    val pressed = buttonPressedColor(enabled, variant.surfaceColor(pressed = true), disabled)
    val color = buttonSurfaceColor(enabled, loading, variant.surfaceColor(pressed = false), pressed, disabled)
    PushlogPressableSurface(
        color = color,
        pressedColor = pressed,
        shape = shape,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.then(semanticsModifier),
        loading = loading,
        shadow = Modifier.pushlogShadow(PushlogTheme.shadow.s3, shape),
        interactionSource = interactionSource,
    ) {
        PushlogLoadingContent(loading, PushlogTheme.dimension.x4, variant.trackColor(), contentColor, content)
    }
}

@PushlogPreview
@Composable
private fun PushlogContextualFloatingButtonPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3),
        ) {
            val arrow = rememberVectorPainter(PreviewSampleIcons.ArrowUp)
            PushlogContextualFloatingButtonVariant.entries.forEach { variant ->
                Row(horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x3)) {
                    PushlogContextualFloatingButton(text = "맨 위로", onClick = {}, variant = variant, prefixIcon = arrow)
                    PushlogContextualFloatingButton(text = "맨 위로", onClick = {}, variant = variant, enabled = false)
                    PushlogContextualFloatingButton(text = "맨 위로", onClick = {}, variant = variant, loading = true)
                }
            }
            PushlogContextualFloatingButton(icon = arrow, contentDescription = "맨 위로", onClick = {})
        }
    }
}
