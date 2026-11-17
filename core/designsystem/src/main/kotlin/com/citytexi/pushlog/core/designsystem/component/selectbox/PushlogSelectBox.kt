package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.pressable.PressScaleScope
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomark
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkSize
import com.citytexi.pushlog.core.designsystem.component.radio.PushlogRadiomarkTone
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import kotlin.math.ceil

// SEED select-box.yaml: strokeWidth 1px. 같은 크기의 토큰이 없다.
private val StrokeWidth = 1.dp

// SEED select-box.yaml: selected.root.strokeWidth 2px. 같은 크기의 토큰이 없다.
private val SelectedStrokeWidth = 2.dp

// SEED select-box.yaml: prefixIcon.size 22px. 같은 크기의 토큰이 없다.
private val PrefixIconSize = 22.dp

private const val DESCRIPTION_MAX_LINES = 2

/**
 * 라벨·설명과 선택 마크를 담은 선택 카드. 카드 전체가 탭 하나이고, 눌리면 카드는 그대로 두고 내용만 준다.
 *
 * 선택 상태와 최대 선택 수 제한은 호출하는 쪽이 갖는다. 단일 선택에서 이미 선택된 박스를 눌러도 [onClick]이 불린다.
 * [showControl]이 false여도 접근성 역할은 [type]을 따른다.
 * 고르는 즉시 실행하는 버튼으로 쓰지 말고 별도 확인 버튼으로 반영한다(SEED 가이드).
 * [layout]이 null이면 감싸는 그룹이 주는 값을 쓰고, 그룹 밖이면 HORIZONTAL이다.
 * [footer]는 줄 아래에 놓이고 [footerVisibility]에 따라 열고 닫는다. 카드 탭 영역 안이고 눌리면 내용과 함께 준다.
 * 안쪽 여백은 [footer]가 직접 준다(SEED 예시: 좌우·아래 x5). [footer]는 `Box` 하나에 놓이므로 루트를 하나로 둔다. 여럿이면 겹친다.
 * [suffix]와 [footer]에는 intrinsic 측정을 지원하지 않는 레이아웃(`LazyColumn`, `BoxWithConstraints`, `SubcomposeLayout`)을 넣지 않는다.
 * 여러 열 그룹이 intrinsic 높이로 줄을 맞춘다.
 */
@Composable
fun PushlogSelectBox(
    selected: Boolean,
    onClick: () -> Unit,
    type: PushlogSelectBoxType,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    prefixIcon: Painter? = null,
    layout: PushlogSelectBoxLayout? = null,
    showControl: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    suffix: (@Composable () -> Unit)? = null,
    footerVisibility: PushlogSelectBoxFooterVisibility = PushlogSelectBoxFooterVisibility.WHEN_SELECTED,
    footer: (@Composable () -> Unit)? = null,
) {
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val resolvedLayout = layout ?: LocalSelectBoxLayout.current
    val radius = PushlogTheme.radius.r3
    val shape = RoundedCornerShape(radius)
    val color = PushlogTheme.color
    val motion = PushlogTheme.motion
    // 같은 색조의 alpha 0에서 번지게 해 중간에 다른 색이 지나가지 않는다. SEED strokeDuration 0.1s = d2.
    val selectedStrokeColor =
        animateColorAsState(
            targetValue = color.stroke.neutralContrast.copy(alpha = if (enabled && selected) 1f else 0f),
            animationSpec = tween(motion.duration.d2, easing = motion.timingFunction.easing),
            label = "SelectBoxSelectedStroke",
        )
    val baseStrokeWidth = if (!enabled && selected) SelectedStrokeWidth else StrokeWidth
    PushlogPressableSurface(
        color = color.bg.transparent,
        pressedColor = color.bg.transparentPressed,
        shape = shape,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.selectedStroke(selectedStrokeColor, radius),
        selected = selected,
        selectionMode = type.selectionMode(),
        scaleScope = PressScaleScope.CONTENT,
        border = BorderStroke(baseStrokeWidth, color.stroke.neutralWeak),
        interactionSource = source,
    ) {
        SelectBoxContent(
            layout = resolvedLayout,
            label = label,
            description = description,
            prefixIcon = prefixIcon,
            enabled = enabled,
            suffix = suffix,
            type = type,
            selected = selected,
            showControl = showControl,
            interactionSource = source,
            footer = footer,
            footerVisible = footerVisibility.isVisible(selected),
        )
    }
}

/**
 * 바깥 선택 테두리. 표면과 기본 테두리 위에 그려진다(SEED ::after). 표면은 줄지 않으므로 테두리도 그대로다.
 * 색은 그리기 단계에서만 읽어 페이드 중에 카드를 다시 구성하지 않는다. 모양과 위치는 `border`와 같다(선 안쪽 끝이 카드 안).
 */
private fun Modifier.selectedStroke(
    color: State<Color>,
    radius: Dp,
): Modifier =
    drawWithCache {
        val strokePx = ceil(SelectedStrokeWidth.toPx())
        val half = strokePx / 2
        val corner = (minOf(radius.toPx(), size.minDimension / 2) - half).coerceAtLeast(0f)
        val stroke = Stroke(strokePx)
        onDrawWithContent {
            drawContent()
            drawRoundRect(
                color = color.value,
                topLeft = Offset(half, half),
                size = Size(size.width - strokePx, size.height - strokePx),
                cornerRadius = CornerRadius(corner),
                style = stroke,
            )
        }
    }

@Composable
private fun SelectBoxContent(
    layout: PushlogSelectBoxLayout,
    label: String,
    description: String?,
    prefixIcon: Painter?,
    enabled: Boolean,
    suffix: (@Composable () -> Unit)?,
    type: PushlogSelectBoxType,
    selected: Boolean,
    showControl: Boolean,
    interactionSource: InteractionSource,
    footer: (@Composable () -> Unit)?,
    footerVisible: Boolean,
) {
    val metrics = layout.metrics()
    // 그룹 밖에서 늘 채우면 높이 제한이 있는 부모(Column)의 남은 높이를 모두 차지한다.
    val height = if (LocalSelectBoxFillsRow.current) Modifier.fillMaxHeight() else Modifier
    Column(
        modifier = Modifier.fillMaxWidth().then(height),
        verticalArrangement = contentArrangement(layout, hasFooter = footer != null),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    start = metrics.paddingStart,
                    end = metrics.paddingEnd,
                    top = metrics.paddingVertical,
                    bottom = metrics.paddingVertical,
                ),
            horizontalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x1Half),
            verticalAlignment = if (layout == PushlogSelectBoxLayout.HORIZONTAL) Alignment.CenterVertically else Alignment.Top,
        ) {
            SelectBoxTrigger(layout, metrics, label, description, prefixIcon, enabled, Modifier.weight(1f))
            suffix?.invoke()
            if (showControl) SelectBoxControl(type, selected, enabled, interactionSource)
        }
        if (footer != null) SelectBoxFooter(footerVisible, footer)
    }
}

// 줄 높이를 받았을 때만 의미가 있다. footer가 있으면 줄은 위, footer는 아래에 붙는다(닫힌 footer는 높이 0으로 남는다).
private fun contentArrangement(
    layout: PushlogSelectBoxLayout,
    hasFooter: Boolean,
): Arrangement.Vertical =
    when {
        hasFooter -> Arrangement.SpaceBetween
        layout == PushlogSelectBoxLayout.HORIZONTAL -> Arrangement.Center
        else -> Arrangement.Top
    }

@Composable
private fun SelectBoxTrigger(
    layout: PushlogSelectBoxLayout,
    metrics: SelectBoxMetrics,
    label: String,
    description: String?,
    prefixIcon: Painter?,
    enabled: Boolean,
    modifier: Modifier,
) {
    val gap = Arrangement.spacedBy(metrics.contentGap)
    when (layout) {
        PushlogSelectBoxLayout.HORIZONTAL ->
            Row(modifier, horizontalArrangement = gap, verticalAlignment = Alignment.CenterVertically) {
                if (prefixIcon != null) SelectBoxPrefixIcon(prefixIcon, enabled)
                SelectBoxTexts(label, description, enabled, Modifier.weight(1f))
            }
        PushlogSelectBoxLayout.VERTICAL ->
            Column(modifier, verticalArrangement = gap) {
                if (prefixIcon != null) SelectBoxPrefixIcon(prefixIcon, enabled)
                SelectBoxTexts(label, description, enabled, Modifier)
            }
    }
}

@Composable
private fun SelectBoxPrefixIcon(
    painter: Painter,
    enabled: Boolean,
) {
    val fg = PushlogTheme.color.fg
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(PrefixIconSize),
        tint = if (enabled) fg.neutral else fg.disabled,
    )
}

@Composable
private fun SelectBoxTexts(
    label: String,
    description: String?,
    enabled: Boolean,
    modifier: Modifier,
) {
    val fg = PushlogTheme.color.fg
    val typography = PushlogTheme.typography
    Column(
        modifier = modifier.padding(end = PushlogTheme.dimension.x1),
        verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x0Half),
    ) {
        Text(text = label, style = typography.t5Medium, color = if (enabled) fg.neutral else fg.disabled)
        if (description != null) {
            Text(
                text = description,
                style = typography.t3Regular,
                color = if (enabled) fg.neutralMuted else fg.disabled,
                maxLines = DESCRIPTION_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// 카드가 내용 축소로 마크까지 줄이므로 마크는 스스로 줄지 않는다.
@Composable
private fun SelectBoxControl(
    type: PushlogSelectBoxType,
    selected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource,
) {
    when (type) {
        PushlogSelectBoxType.SINGLE ->
            PushlogRadiomark(
                selected = selected,
                tone = PushlogRadiomarkTone.NEUTRAL,
                size = PushlogRadiomarkSize.MEDIUM,
                enabled = enabled,
                scaleOnPress = false,
                interactionSource = interactionSource,
            )
        PushlogSelectBoxType.MULTIPLE -> SelectBoxCheckmark(selected, enabled, interactionSource)
    }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxPreview() {
    PushlogPreviewTheme {
        Column(
            Modifier.padding(PushlogTheme.dimension.x4),
            verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2),
        ) {
            PushlogSelectBox(
                selected = true,
                onClick = {},
                type = PushlogSelectBoxType.SINGLE,
                label = "사과",
                description = "빨갛고 단단한 과일",
            )
            PushlogSelectBox(selected = false, onClick = {}, type = PushlogSelectBoxType.SINGLE, label = "바나나")
            PushlogSelectBox(selected = true, onClick = {}, type = PushlogSelectBoxType.MULTIPLE, label = "포도")
        }
    }
}
