package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.pushlogFieldErrorSemantics
import com.citytexi.pushlog.core.designsystem.component.field.spokenText
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/**
 * 한 줄·여러 줄 입력이 같이 쓰는 본체. [LocalPushlogFieldState]는 안쪽을 비우기 전에 읽어 의미 정보에 쓴다.
 * 입력 노드에는 오류만 붙이고, 라벨·보조 문구는 상자 안 의미 전용 자식으로 합친다(M3 라벨과 같은 길).
 * [multiline]이면 상자 크기와 앞뒤 요소 정렬이 여러 줄 모양이 된다.
 */
@Composable
internal fun PushlogTextInputBase(
    state: TextFieldState,
    modifier: Modifier,
    variant: PushlogTextInputVariant,
    multiline: Boolean,
    lineLimits: TextFieldLineLimits,
    placeholder: String?,
    prefixIcon: Painter?,
    prefixText: String?,
    suffixText: String?,
    suffixIcon: Painter?,
    maxGraphemeCount: Int?,
    invalid: Boolean,
    enabled: Boolean,
    readOnly: Boolean,
    keyboardOptions: KeyboardOptions,
    onKeyboardAction: KeyboardActionHandler?,
    inputTransformation: InputTransformation?,
    outputTransformation: OutputTransformation?,
    interactionSource: MutableInteractionSource?,
) {
    val field = LocalPushlogFieldState.current
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val metrics = variant.metrics(multiline = multiline)
    val spokenText = field.spokenText()
    val showPlaceholder by remember(state) { derivedStateOf { state.text.isEmpty() } }
    val transformation =
        remember(inputTransformation, maxGraphemeCount) {
            val limit = maxGraphemeCount?.let { GraphemeLimitTransformation(it) }
            if (inputTransformation != null && limit != null) inputTransformation.then(limit) else inputTransformation ?: limit
        }
    ResetPushlogFieldState {
        BasicTextField(
            state = state,
            modifier = modifier.pushlogFieldErrorSemantics(field, invalid),
            enabled = enabled,
            readOnly = readOnly,
            inputTransformation = transformation,
            textStyle = metrics.textStyle.copy(color = variant.valueColor(enabled, readOnly)),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = lineLimits,
            interactionSource = source,
            cursorBrush = SolidColor(PushlogTheme.color.fg.neutral),
            outputTransformation = outputTransformation,
            decorator = { innerTextField ->
                TextInputDecoration(
                    variant = variant,
                    metrics = metrics,
                    focused = focused,
                    invalid = invalid,
                    enabled = enabled,
                    readOnly = readOnly,
                    showPlaceholder = showPlaceholder,
                    placeholder = placeholder,
                    spokenText = spokenText,
                    prefixIcon = prefixIcon,
                    prefixText = prefixText,
                    suffixText = suffixText,
                    suffixIcon = suffixIcon,
                    multiline = multiline,
                    modifier = Modifier.fillMaxWidth(),
                    innerTextField = innerTextField,
                )
            },
        )
    }
}
