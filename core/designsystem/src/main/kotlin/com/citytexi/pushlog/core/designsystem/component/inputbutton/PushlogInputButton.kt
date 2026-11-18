package com.citytexi.pushlog.core.designsystem.component.inputbutton

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.citytexi.pushlog.core.designsystem.R
import com.citytexi.pushlog.core.designsystem.component.clearbutton.ClearButton
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.pushlogFieldSemantics
import com.citytexi.pushlog.core.designsystem.component.pressable.PressScaleScope
import com.citytexi.pushlog.core.designsystem.component.pressable.PushlogPressableSurface
import com.citytexi.pushlog.core.designsystem.component.pressable.pushlogPressScale
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInputVariant
import com.citytexi.pushlog.core.designsystem.component.textinput.textInputStroke
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED input-button.yaml 지우기 아이콘 22px. 토큰이 없다.
private val ClearIconSize = 22.dp

// 지우기 탭 영역을 search-input과 같은 44dp로 넓힌다(22 + 11 * 2). 높이 x13(52dp) 안에 들어간다.
private val ClearTouchPadding = 11.dp

// 접근성 이름 조각을 잇는 구분자. field 읽기 문구와 같다.
private const val NAME_SEPARATOR = ", "

/**
 * 피커·시트를 여는 버튼. 글자 입력이 아니어서 값은 [value]로 받아 보여 주기만 하고, 누르면 [onClick]을 부른다.
 * [value]가 null이거나 비어 있으면 [placeholder]가 보인다. [onClear]를 주면 값이 있을 때 지우기 버튼이 보인다.
 * 읽기 전용이면 배경만 비활성 색이고 값 색은 그대로이며 눌리지 않는다.
 * 접근성 이름은 field 라벨(또는 [contentDescription]) + 값(없으면 placeholder)이다. field 밖에서는 [contentDescription]을 준다.
 */
@Composable
fun PushlogInputButton(
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    onClear: (() -> Unit)? = null,
    prefixIcon: Painter? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    suffixIcon: Painter? = null,
    contentDescription: String? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
    interactionSource: MutableInteractionSource? = null,
) {
    val field = LocalPushlogFieldState.current
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val dimension = PushlogTheme.dimension
    val cornerRadius = PushlogTheme.radius.r3
    val color = PushlogTheme.color
    val pressable = enabled && !readOnly
    val shown = value?.takeIf { it.isNotEmpty() }
    val name =
        listOfNotNull(
            contentDescription ?: field.label,
            shown ?: placeholder,
        ).joinToString(NAME_SEPARATOR).takeIf { it.isNotEmpty() }
    val semantics = Modifier.pushlogFieldSemantics(field, invalid, contentDescription = name)
    ResetPushlogFieldState {
        // 위층 Row가 Box의 최소 제약을 그대로 받아 아래층 표면과 크기가 같다. 그래서 축소 배율이 표면 크기 기준이 된다.
        Box(modifier.heightIn(min = dimension.x13), propagateMinConstraints = true) {
            // 아래층: 탭과 눌림 색. 내용이 없어 눌려도 표면은 줄지 않는다(SEED scaleScope: content).
            PushlogPressableSurface(
                color = if (pressable) color.bg.transparent else color.bg.disabled,
                pressedColor = color.bg.transparentPressed,
                shape = RoundedCornerShape(cornerRadius),
                enabled = pressable,
                onClick = onClick,
                modifier = Modifier.matchParentSize().then(semantics),
                scaleScope = PressScaleScope.CONTENT,
                interactionSource = source,
            ) {}
            // 테두리는 표면 배경 위에 그려야 하고 눌려도 줄지 않아야 해서 따로 겹친다. 탭과 의미 정보가 없어 터치는 아래로 간다.
            Spacer(Modifier.matchParentSize().textInputStroke(PushlogTextInputVariant.OUTLINE, cornerRadius, emphasized = false, invalid))
            // 위층: 내용만 줄인다. 탭 modifier가 없어 빈 곳 터치는 아래층으로 가고, 지우기 버튼은 자기 탭을 먼저 받는다.
            Row(
                modifier = Modifier.pushlogPressScale(source, pressable).padding(horizontal = dimension.x4),
                horizontalArrangement = Arrangement.spacedBy(dimension.x2Half),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InputButtonContent(
                    value = shown,
                    placeholder = placeholder,
                    onClear = onClear.takeIf { shown != null && pressable },
                    prefixIcon = prefixIcon,
                    prefixText = prefixText,
                    suffixText = suffixText,
                    suffixIcon = suffixIcon,
                    enabled = enabled,
                )
            }
        }
    }
}

// 글자·아이콘은 이름에 이미 들어가 따로 읽히지 않게 숨긴다. 지우기 버튼만 의미 정보를 남긴다.
@Composable
private fun RowScope.InputButtonContent(
    value: String?,
    placeholder: String?,
    onClear: (() -> Unit)?,
    prefixIcon: Painter?,
    prefixText: String?,
    suffixText: String?,
    suffixIcon: Painter?,
    enabled: Boolean,
) {
    val fg = PushlogTheme.color.fg
    val iconColor = if (enabled) fg.neutralMuted else fg.disabled
    val affixColor = if (enabled) fg.neutralSubtle else fg.disabled
    prefixIcon?.let { AffixIcon(it, iconColor) }
    prefixText?.let { AffixText(it, affixColor) }
    val textColor =
        when {
            !enabled -> fg.disabled
            value != null -> fg.neutral
            else -> fg.placeholder
        }
    Text(
        text = value ?: placeholder.orEmpty(),
        modifier = Modifier.weight(1f).clearAndSetSemantics {},
        style = PushlogTheme.typography.t5Regular,
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
    onClear?.let {
        ClearButton(
            onClick = it,
            contentDescription = stringResource(R.string.designsystem_clear),
            iconSize = ClearIconSize,
            color = fg.neutralSubtle,
            modifier = Modifier.overhangHorizontally(ClearTouchPadding),
            touchPadding = ClearTouchPadding,
        )
    }
    suffixText?.let { AffixText(it, affixColor) }
    suffixIcon?.let { AffixIcon(it, iconColor) }
}

/**
 * 가로로 [overhang]만큼 양옆에 걸치게 놓는다. Row에는 그만큼 좁게 알려 아이콘 위치와 이웃 간격은 넓히기 전과 같고,
 * 넓어진 탭 영역만 간격 위로 겹친다. 음수 padding은 쓸 수 없고 spacedBy는 항목마다 줄일 수 없어서 크기를 직접 알린다.
 */
private fun Modifier.overhangHorizontally(overhang: Dp): Modifier =
    layout { measurable, constraints ->
        val extra = overhang.roundToPx()
        val placeable = measurable.measure(constraints.offset(horizontal = extra * 2))
        layout((placeable.width - extra * 2).coerceAtLeast(0), placeable.height) { placeable.place(-extra, 0) }
    }

@Composable
private fun AffixIcon(
    painter: Painter,
    tint: Color,
) {
    Icon(painter = painter, contentDescription = null, modifier = Modifier.size(PushlogTheme.dimension.x5), tint = tint)
}

@Composable
private fun AffixText(
    text: String,
    color: Color,
) {
    Text(
        text = text,
        modifier = Modifier.clearAndSetSemantics {},
        style = PushlogTheme.typography.t5Regular,
        color = color,
        maxLines = 1,
    )
}

@PushlogPreview
@Composable
private fun PushlogInputButtonPreview() {
    PushlogPreviewTheme { PushlogInputButton(value = null, onClick = {}, placeholder = "날짜 선택", contentDescription = "생일") }
}

@PushlogPreview
@Composable
private fun PushlogInputButtonClearPreview() {
    PushlogPreviewTheme {
        PushlogInputButton(
            value = "1990.01.01",
            onClick = {},
            onClear = {},
            contentDescription = "생일",
            suffixIcon = rememberVectorPainter(PreviewSampleIcons.ChevronDown),
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogInputButtonStatesPreview() {
    PushlogPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
            PushlogInputButton(value = "오류", onClick = {}, contentDescription = "생일", invalid = true)
            PushlogInputButton(value = "읽기 전용", onClick = {}, contentDescription = "생일", readOnly = true)
            PushlogInputButton(value = "비활성", onClick = {}, contentDescription = "생일", enabled = false)
        }
    }
}
