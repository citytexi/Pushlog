package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.citytexi.pushlog.core.designsystem.component.textinput.PushlogTextInput
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED field 머리 보조 글자 왼쪽 간격. 토큰이 없어 글자 크기를 따라가는 sp로 둔다.
private const val INDICATOR_TEXT_START_SP = 4

// 꼬리 글자 한 줄 높이를 잴 때 쓰는 글자.
private const val LINE_PROBE = "가"

/**
 * 입력 위아래에 라벨, 설명, 오류, 글자 수를 붙이는 wrapper.
 * 안쪽 입력에는 상태와 문구를 [LocalPushlogFieldState]로 내려준다.
 * 머리와 꼬리는 화면에만 그린다. 라벨, 필수 표시, 보조 글자, 설명, 글자 수는 안쪽 입력이 읽어 두 번 읽히지 않는다.
 * 오류 문구는 [invalid]일 때만 보이고, 그때 설명은 화면에서 숨지만 입력 semantics에는 남는다.
 */
@Composable
fun PushlogField(
    modifier: Modifier = Modifier,
    label: String? = null,
    labelWeight: PushlogFieldLabelWeight = PushlogFieldLabelWeight.MEDIUM,
    showRequiredIndicator: Boolean = false,
    indicatorText: String? = null,
    description: String? = null,
    descriptionIcon: Painter? = null,
    errorMessage: String? = null,
    characterCount: Int? = null,
    maxCharacterCount: Int? = null,
    invalid: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    content: @Composable () -> Unit,
) {
    val state =
        PushlogFieldState(
            invalid = invalid,
            enabled = enabled,
            readOnly = readOnly,
            label = label,
            description = description,
            errorMessage = errorMessage,
            required = showRequiredIndicator,
            indicatorText = indicatorText,
            characterCount = characterCount,
            maxCharacterCount = maxCharacterCount,
        )
    val showError = invalid && errorMessage != null
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(PushlogTheme.dimension.x2)) {
        if (label != null || indicatorText != null) {
            FieldHeader(label, labelWeight, showRequiredIndicator, indicatorText)
        }
        CompositionLocalProvider(LocalPushlogFieldState provides state) { content() }
        if (description != null || showError || characterCount != null) {
            FieldFooter(
                description = description,
                descriptionIcon = descriptionIcon,
                errorMessage = errorMessage.takeIf { showError },
                invalid = invalid,
                characterCount = characterCount,
                maxCharacterCount = maxCharacterCount,
            )
        }
    }
}

@Composable
private fun FieldHeader(
    label: String?,
    weight: PushlogFieldLabelWeight,
    showRequiredIndicator: Boolean,
    indicatorText: String?,
) {
    val typography = PushlogTheme.typography
    Row(Modifier.padding(horizontal = PushlogTheme.dimension.x0Half).clearAndSetSemantics {}) {
        if (label != null) PushlogFieldLabel(text = label, weight = weight)
        if (showRequiredIndicator) FieldRequiredIndicator()
        if (indicatorText != null) {
            Text(
                text = indicatorText,
                modifier = Modifier.padding(start = with(LocalDensity.current) { INDICATOR_TEXT_START_SP.sp.toDp() }),
                style = typography.t4Regular.copy(lineHeight = typography.t5Regular.lineHeight),
                color = PushlogTheme.color.fg.neutralSubtle,
            )
        }
    }
}

@Composable
private fun FieldFooter(
    description: String?,
    descriptionIcon: Painter?,
    errorMessage: String?,
    invalid: Boolean,
    characterCount: Int?,
    maxCharacterCount: Int?,
) {
    val dimension = PushlogTheme.dimension
    Row(
        modifier = Modifier.padding(horizontal = dimension.x0Half).clearAndSetSemantics {},
        horizontalArrangement = Arrangement.spacedBy(dimension.x2),
    ) {
        Row(Modifier.weight(1f)) {
            if (errorMessage != null) {
                val critical = PushlogTheme.color.fg.critical
                FieldMessage(errorMessage, critical) {
                    Canvas(Modifier.size(dimension.x4)) { drawExclamationCircleIcon(critical) }
                }
            } else if (description != null) {
                val icon: (@Composable () -> Unit)? =
                    descriptionIcon?.let {
                        {
                            Icon(
                                painter = it,
                                contentDescription = null,
                                modifier = Modifier.size(dimension.x4),
                                tint = PushlogTheme.color.fg.neutralSubtle,
                            )
                        }
                    }
                FieldMessage(description, PushlogTheme.color.fg.neutralSubtle, icon)
            }
        }
        if (characterCount != null) FieldCharacterCount(characterCount, maxCharacterCount, invalid)
    }
}

@Composable
private fun FieldMessage(
    text: String,
    color: Color,
    icon: (@Composable () -> Unit)?,
) {
    val style = PushlogTheme.typography.t4Regular
    if (icon != null) {
        // 아이콘을 글자 첫 줄 높이 상자 가운데에 둔다. 여러 줄이어도 첫 줄에 맞는다.
        // 큰 글꼴 배율은 비선형이라 줄 높이 sp를 dp로 바꾸면 실제 줄과 어긋난다. 한 줄을 재서 쓴다.
        val measurer = rememberTextMeasurer()
        val density = LocalDensity.current
        val lineHeight =
            remember(measurer, style, density) {
                with(density) {
                    measurer
                        .measure(LINE_PROBE, style)
                        .size.height
                        .toDp()
                }
            }
        Box(Modifier.height(lineHeight), contentAlignment = Alignment.Center) { icon() }
        Spacer(Modifier.width(PushlogTheme.dimension.x1Half))
    }
    Text(text = text, style = style, color = color)
}

@Composable
private fun FieldCharacterCount(
    characterCount: Int,
    maxCharacterCount: Int?,
    invalid: Boolean,
) {
    val colors = PushlogTheme.color.fg
    val style = PushlogTheme.typography.t4Regular
    Row(verticalAlignment = Alignment.Top) {
        Text(text = "$characterCount", style = style, color = if (invalid) colors.critical else colors.neutral)
        if (maxCharacterCount != null) {
            Text(
                text = "/$maxCharacterCount",
                style = style,
                color = if (invalid) colors.critical else colors.neutralSubtle,
            )
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogFieldDefaultPreview() {
    PushlogPreviewTheme { PushlogField(label = "이름", description = "실명을 입력하세요") { PreviewInput() } }
}

@PushlogPreview
@Composable
private fun PushlogFieldRequiredPreview() {
    PushlogPreviewTheme {
        PushlogField(label = "이름", showRequiredIndicator = true, indicatorText = "선택") { PreviewInput() }
    }
}

@PushlogPreview
@Composable
private fun PushlogFieldErrorPreview() {
    PushlogPreviewTheme {
        PushlogField(label = "이름", errorMessage = "필수 항목", description = "숫자만", invalid = true) { PreviewInput() }
    }
}

@PushlogPreview
@Composable
private fun PushlogFieldCharacterCountPreview() {
    PushlogPreviewTheme {
        PushlogField(label = "소개", characterCount = 3, maxCharacterCount = 40) { PreviewInput() }
    }
}

@Composable
private fun PreviewInput() {
    PushlogTextInput(remember { TextFieldState() })
}
