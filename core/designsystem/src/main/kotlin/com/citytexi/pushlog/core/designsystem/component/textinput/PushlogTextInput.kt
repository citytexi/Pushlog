package com.citytexi.pushlog.core.designsystem.component.textinput

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
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
    PushlogTextInputBase(
        state = state,
        modifier = modifier,
        variant = variant,
        multiline = false,
        lineLimits = TextFieldLineLimits.SingleLine,
        placeholder = placeholder,
        prefixIcon = prefixIcon,
        prefixText = prefixText,
        suffixText = suffixText,
        suffixIcon = suffixIcon,
        maxGraphemeCount = maxGraphemeCount,
        invalid = invalid,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        inputTransformation = inputTransformation,
        outputTransformation = outputTransformation,
        interactionSource = interactionSource,
    )
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
