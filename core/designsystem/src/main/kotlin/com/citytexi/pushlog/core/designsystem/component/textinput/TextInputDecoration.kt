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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED text-input 테두리 두께와 강조선 전환 시간. 토큰이 없다.
private val STROKE_WIDTH = 1.dp
private val EMPHASIS_STROKE_WIDTH = 2.dp
private const val EMPHASIS_DURATION_MILLIS = 100

/** 기본선을 늘 그리고, emphasized || invalid면 2dp 강조선을 100ms 투명도 전환으로 겹친다. */
@Composable
internal fun Modifier.textInputStroke(
    variant: PushlogTextInputVariant,
    cornerRadius: Dp,
    emphasized: Boolean,
    invalid: Boolean,
): Modifier {
    val stroke = PushlogTheme.color.stroke
    val baseColor = stroke.neutralWeak
    val emphasisColor = if (invalid) stroke.criticalSolid else stroke.neutralContrast
    val alpha =
        animateFloatAsState(
            targetValue = if (emphasized || invalid) 1f else 0f,
            animationSpec = tween(EMPHASIS_DURATION_MILLIS, easing = PushlogTheme.motion.timingFunction.easing),
            label = "textInputStroke",
        )
    return drawBehind {
        drawStroke(variant, STROKE_WIDTH, cornerRadius, baseColor, 1f)
        drawStroke(variant, EMPHASIS_STROKE_WIDTH, cornerRadius, emphasisColor, alpha.value)
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
    Row(
        modifier =
            modifier
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            innerTextField()
        }
        suffixText?.let { DecorationText(it, metrics, affixColor) }
        suffixIcon?.let { DecorationIcon(it, metrics, iconColor) }
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
