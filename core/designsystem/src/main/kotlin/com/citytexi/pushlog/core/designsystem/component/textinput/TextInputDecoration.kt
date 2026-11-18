package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.field.PushlogFieldSpokenText
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED text-input 테두리 두께와 강조선 전환 시간. 토큰이 없다.
private val STROKE_WIDTH = 1.dp
private val EMPHASIS_STROKE_WIDTH = 2.dp
private const val EMPHASIS_DURATION_MILLIS = 100

/**
 * 기본선을 늘 그리고 그 위에 2dp 강조선을 겹친다. 포커스(emphasized)와 오류(invalid)는 각자 100ms로 전환하는 정도다.
 * 강조선 투명도는 둘 중 큰 값, 색은 그 안에서 오류가 차지하는 비율만큼 포커스 색에서 오류 색으로 옮겨 간다.
 * 그래서 포커스 중 오류가 생기거나 걷혀도 색이 튀거나 강조선이 비지 않는다. 두 선을 따로 겹치면 모서리에 아래 선이 비친다.
 */
@Composable
internal fun Modifier.textInputStroke(
    variant: PushlogTextInputVariant,
    cornerRadius: Dp,
    emphasized: Boolean,
    invalid: Boolean,
): Modifier {
    val stroke = PushlogTheme.color.stroke
    val baseColor = stroke.neutralWeak
    val focusColor = stroke.neutralContrast
    val errorColor = stroke.criticalSolid
    val spec = tween<Float>(EMPHASIS_DURATION_MILLIS, easing = PushlogTheme.motion.timingFunction.easing)
    val focusAlpha = animateFloatAsState(if (emphasized) 1f else 0f, spec, label = "textInputFocusStroke")
    val errorAlpha = animateFloatAsState(if (invalid) 1f else 0f, spec, label = "textInputErrorStroke")
    return drawBehind {
        drawStroke(variant, STROKE_WIDTH, cornerRadius, baseColor, 1f)
        val focus = focusAlpha.value
        val error = errorAlpha.value
        val alpha = maxOf(focus, error)
        if (alpha > 0f) {
            val color = lerp(focusColor, errorColor, error / alpha)
            drawStroke(variant, EMPHASIS_STROKE_WIDTH, cornerRadius, color, alpha)
        }
    }
}

private fun DrawScope.drawStroke(
    variant: PushlogTextInputVariant,
    width: Dp,
    cornerRadius: Dp,
    color: Color,
    alpha: Float,
) {
    if (alpha <= 0f) return
    val widthPx = width.toPx()
    when (variant) {
        PushlogTextInputVariant.OUTLINE -> {
            val inset = widthPx / 2
            drawRoundRect(
                color = color,
                topLeft = Offset(inset, inset),
                size = Size(size.width - widthPx, size.height - widthPx),
                cornerRadius = CornerRadius((cornerRadius.toPx() - inset).coerceAtLeast(0f)),
                style = Stroke(widthPx),
                alpha = alpha,
            )
        }
        PushlogTextInputVariant.UNDERLINE ->
            drawRect(
                color = color,
                topLeft = Offset(0f, size.height - widthPx),
                size = Size(size.width, widthPx),
                alpha = alpha,
            )
    }
}

/** 한 줄·여러 줄 입력이 같이 쓰는 상자. 값 자리는 [innerTextField]가 채운다. [multiline]이면 앞뒤 요소를 위쪽에 맞춘다. */
@Composable
internal fun TextInputDecoration(
    variant: PushlogTextInputVariant,
    metrics: TextInputMetrics,
    focused: Boolean,
    invalid: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    showPlaceholder: Boolean,
    placeholder: String?,
    spokenText: String?,
    prefixIcon: Painter?,
    prefixText: String?,
    suffixText: String?,
    suffixIcon: Painter?,
    multiline: Boolean = false,
    modifier: Modifier = Modifier,
    innerTextField: @Composable () -> Unit,
) {
    val fg = PushlogTheme.color.fg
    val affixColor = if (enabled) fg.neutralSubtle else fg.disabled
    val iconColor = if (enabled) fg.neutralMuted else fg.disabled
    // 최소 크기를 넘겨 상자가 바깥 modifier 크기를 그대로 채우게 한다.
    Box(modifier, propagateMinConstraints = true) {
        // 다른 자식보다 먼저 그려 위에 덮이지 않게 한다. 덮인 노드는 접근성 트리에서 빠진다.
        spokenText?.let { PushlogFieldSpokenText(it, Modifier.matchParentSize()) }
        Row(
            modifier =
                Modifier
                    .heightIn(min = metrics.minHeight)
                    .clip(RoundedCornerShape(metrics.cornerRadius))
                    .background(variant.containerColor(enabled, readOnly))
                    .textInputStroke(variant, metrics.cornerRadius, focused, invalid)
                    .padding(horizontal = metrics.paddingX, vertical = metrics.paddingY),
            horizontalArrangement = Arrangement.spacedBy(metrics.gap),
            verticalAlignment = if (multiline) Alignment.Top else Alignment.CenterVertically,
        ) {
            prefixIcon?.let { DecorationIcon(it, metrics, iconColor) }
            prefixText?.let { DecorationText(it, metrics, affixColor) }
            Box(Modifier.weight(1f)) {
                if (showPlaceholder && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = metrics.textStyle,
                        color = variant.placeholderColor(enabled, readOnly),
                        maxLines = if (multiline) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                innerTextField()
            }
            suffixText?.let { DecorationText(it, metrics, affixColor) }
            suffixIcon?.let { DecorationIcon(it, metrics, iconColor) }
        }
    }
}

@Composable
private fun DecorationIcon(
    painter: Painter,
    metrics: TextInputMetrics,
    tint: Color,
) {
    Icon(painter = painter, contentDescription = null, modifier = Modifier.size(metrics.iconSize), tint = tint)
}

@Composable
private fun DecorationText(
    text: String,
    metrics: TextInputMetrics,
    color: Color,
) {
    Text(text = text, style = metrics.textStyle, color = color, maxLines = 1)
}
