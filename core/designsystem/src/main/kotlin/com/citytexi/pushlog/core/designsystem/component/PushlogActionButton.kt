package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.R
import com.citytexi.pushlog.core.designsystem.theme.LocalPushlogDarkTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED action-button.yaml: LARGE 아이콘 크기. 같은 크기의 간격 토큰이 없다.
private val LargeIconSize = 22.dp

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
    val contentColor = contentColor(variant, enabled)
    val dimension = PushlogTheme.dimension
    val iconSize = if (size == PushlogActionButtonSize.LARGE) LargeIconSize else textIconSize(size)
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
        semanticsModifier = Modifier.semantics(mergeDescendants = true) {},
    ) {
        Row(
            modifier =
                Modifier.padding(
                    horizontal =
                        when (size) {
                            PushlogActionButtonSize.SMALL -> dimension.x3Half
                            PushlogActionButtonSize.MEDIUM -> dimension.x4
                            PushlogActionButtonSize.LARGE -> dimension.x5
                        },
                    vertical =
                        when (size) {
                            PushlogActionButtonSize.SMALL -> dimension.x2
                            PushlogActionButtonSize.MEDIUM -> dimension.x2Half
                            PushlogActionButtonSize.LARGE -> dimension.x3Half
                        },
                ),
            horizontalArrangement =
                Arrangement.spacedBy(
                    when (size) {
                        PushlogActionButtonSize.SMALL -> dimension.x1
                        PushlogActionButtonSize.MEDIUM -> dimension.x1Half
                        PushlogActionButtonSize.LARGE -> dimension.x2
                    },
                    Alignment.CenterHorizontally,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (prefixIcon != null) {
                Icon(painter = prefixIcon, contentDescription = null, modifier = Modifier.size(iconSize), tint = contentColor)
            }
            Text(text = text, style = labelStyle(size), color = contentColor)
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
    val contentColor = contentColor(variant, enabled)
    val dimension = PushlogTheme.dimension
    ActionButtonSurface(
        variant = variant,
        size = size,
        enabled = enabled,
        loading = loading,
        onClick = onClick,
        interactionSource = interactionSource,
        contentColor = contentColor,
        minWidth =
            when (size) {
                PushlogActionButtonSize.SMALL -> dimension.x9
                PushlogActionButtonSize.MEDIUM -> dimension.x10
                PushlogActionButtonSize.LARGE -> dimension.x13
            },
        modifier = modifier,
        semanticsModifier = Modifier.semantics(mergeDescendants = true) { this.contentDescription = contentDescription },
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier =
                Modifier
                    .padding(
                        when (size) {
                            PushlogActionButtonSize.SMALL -> dimension.x2
                            PushlogActionButtonSize.MEDIUM -> dimension.x2Half
                            PushlogActionButtonSize.LARGE -> dimension.x3Half
                        },
                    ).size(
                        when (size) {
                            PushlogActionButtonSize.SMALL -> dimension.x4
                            PushlogActionButtonSize.MEDIUM -> dimension.x4Half
                            PushlogActionButtonSize.LARGE -> LargeIconSize
                        },
                    ),
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
    val radius = PushlogTheme.radius
    val bg = PushlogTheme.color.bg
    val minHeight = minHeight(size)
    val corner = if (size == PushlogActionButtonSize.LARGE) radius.r3 else radius.r2
    val progressSize = progressSize(size)
    val disabledSurface = if (variant == PushlogActionButtonVariant.GHOST) bg.transparent else bg.disabled
    val pressedColor = if (enabled) surfaceColor(variant, pressed = true) else disabledSurface
    val color =
        if (!enabled) {
            disabledSurface
        } else if (loading) {
            pressedColor
        } else {
            surfaceColor(variant, pressed = false)
        }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val loadingDescription = stringResource(R.string.designsystem_loading)
    Box(
        modifier =
            modifier
                .pushlogPressable(
                    interactionSource = source,
                    color = color,
                    pressedColor = pressedColor,
                    shape = RoundedCornerShape(corner),
                    enabled = enabled,
                    // loading을 넘기면 clickable이 enabled=false가 되어 접근성에 Disabled가 붙는다.
                    // 로딩은 눌림 색을 직접 칠하고 클릭만 삼켜서 "클릭 불가지만 비활성은 아님"을 만든다.
                    loading = false,
                    onClick = { if (!loading) onClick() },
                ).defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .then(semanticsModifier)
                .semantics { if (loading) stateDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.alpha(if (loading) 0f else 1f)) { content() }
        if (loading) {
            PushlogProgressCircle(size = progressSize, trackColor = trackColor(variant), rangeColor = contentColor)
        }
    }
}

@Composable
private fun minHeight(size: PushlogActionButtonSize): Dp =
    when (size) {
        PushlogActionButtonSize.SMALL -> PushlogTheme.dimension.x9
        PushlogActionButtonSize.MEDIUM -> PushlogTheme.dimension.x10
        PushlogActionButtonSize.LARGE -> PushlogTheme.dimension.x13
    }

@Composable
private fun progressSize(size: PushlogActionButtonSize): Dp =
    when (size) {
        PushlogActionButtonSize.SMALL -> PushlogTheme.dimension.x3Half
        PushlogActionButtonSize.MEDIUM -> PushlogTheme.dimension.x4
        PushlogActionButtonSize.LARGE -> PushlogTheme.dimension.x4Half
    }

@Composable
private fun textIconSize(size: PushlogActionButtonSize): Dp =
    if (size == PushlogActionButtonSize.SMALL) PushlogTheme.dimension.x3Half else PushlogTheme.dimension.x4

@Composable
private fun labelStyle(size: PushlogActionButtonSize): TextStyle =
    if (size == PushlogActionButtonSize.LARGE) PushlogTheme.typography.t6Bold else PushlogTheme.typography.t4Bold

@Composable
private fun surfaceColor(
    variant: PushlogActionButtonVariant,
    pressed: Boolean,
): Color {
    val bg = PushlogTheme.color.bg
    return when (variant) {
        PushlogActionButtonVariant.BRAND_SOLID -> if (pressed) bg.brandSolidPressed else bg.brandSolid
        PushlogActionButtonVariant.NEUTRAL_SOLID -> if (pressed) bg.neutralSolidPressed else bg.neutralSolid
        PushlogActionButtonVariant.NEUTRAL_WEAK -> if (pressed) bg.neutralWeakPressed else bg.neutralWeak
        PushlogActionButtonVariant.CRITICAL_SOLID -> if (pressed) bg.criticalSolidPressed else bg.criticalSolid
        PushlogActionButtonVariant.GHOST -> if (pressed) bg.transparentPressed else bg.transparent
    }
}

@Composable
private fun contentColor(
    variant: PushlogActionButtonVariant,
    enabled: Boolean,
): Color {
    val fg = PushlogTheme.color.fg
    return when {
        !enabled -> fg.disabled
        variant == PushlogActionButtonVariant.BRAND_SOLID -> fg.onBrandSolid
        variant == PushlogActionButtonVariant.NEUTRAL_SOLID -> fg.onNeutralSolid
        variant == PushlogActionButtonVariant.CRITICAL_SOLID -> fg.onCriticalSolid
        else -> fg.neutral
    }
}

@Composable
private fun trackColor(variant: PushlogActionButtonVariant): Color {
    val dark = LocalPushlogDarkTheme.current
    val onSolid =
        variant == PushlogActionButtonVariant.BRAND_SOLID ||
            variant == PushlogActionButtonVariant.NEUTRAL_SOLID ||
            variant == PushlogActionButtonVariant.CRITICAL_SOLID
    return when {
        onSolid -> if (dark) DarkPalette.staticWhiteAlpha300 else LightPalette.staticWhiteAlpha300
        else -> if (dark) DarkPalette.gray500 else LightPalette.gray500
    }
}

@PushlogPreview
@Composable
private fun PushlogActionButtonPreview() {
    PushlogTheme {
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
