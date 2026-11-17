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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.component.field.pushlogFieldSemantics
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

/**
 * 한 줄 글자 입력. [invalid], [enabled], [readOnly]를 따로 주지 않으면 감싼 `PushlogField`의 상태를 따른다.
 * [maxGraphemeCount]는 [inputTransformation] 뒤에 이어 붙는다.
 */
@Composable
fun PushlogTextInput(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    variant: PushlogTextInputVariant = PushlogTextInputVariant.OUTLINE,
    placeholder: String? = null,
    prefixIcon: Painter? = null,
    prefixText: String? = null,
    suffixText: String? = null,
    suffixIcon: Painter? = null,
    maxGraphemeCount: Int? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    inputTransformation: InputTransformation? = null,
    outputTransformation: OutputTransformation? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    val field = LocalPushlogFieldState.current
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val focused by source.collectIsFocusedAsState()
    val metrics = variant.metrics(multiline = false)
    val transformation =
        remember(inputTransformation, maxGraphemeCount) {
            val limit = maxGraphemeCount?.let { GraphemeLimitTransformation(it) }
            if (inputTransformation != null && limit != null) inputTransformation.then(limit) else inputTransformation ?: limit
        }
    ResetPushlogFieldState {
        BasicTextField(
            state = state,
            modifier = modifier.pushlogFieldSemantics(field, invalid),
            enabled = enabled,
            readOnly = readOnly,
            inputTransformation = transformation,
            textStyle = metrics.textStyle.copy(color = variant.valueColor(enabled, readOnly)),
            keyboardOptions = keyboardOptions,
            onKeyboardAction = onKeyboardAction,
            lineLimits = TextFieldLineLimits.SingleLine,
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
                    showPlaceholder = state.text.isEmpty(),
                    placeholder = placeholder,
                    prefixIcon = prefixIcon,
                    prefixText = prefixText,
                    suffixText = suffixText,
                    suffixIcon = suffixIcon,
                    modifier = Modifier.fillMaxWidth(),
                    innerTextField = innerTextField,
                )
            },
        )
    }
}

@PushlogPreview
@Composable
private fun PushlogTextInputOutlinePreview() {
    PushlogPreviewTheme {
        PushlogTextInput(state = remember { TextFieldState("값") }, placeholder = "입력")
    }
}

@PushlogPreview
@Composable
private fun PushlogTextInputUnderlinePreview() {
    PushlogPreviewTheme {
        PushlogTextInput(
            state = remember { TextFieldState() },
            variant = PushlogTextInputVariant.UNDERLINE,
            placeholder = "입력",
            suffixText = "원",
        )
    }
}
