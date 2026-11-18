package com.citytexi.pushlog.core.designsystem.component.searchinput

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.R
import com.citytexi.pushlog.core.designsystem.component.clearbutton.ClearButton
import com.citytexi.pushlog.core.designsystem.component.field.ResetPushlogFieldState
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED search-input.yaml 높이 44px.
private val SearchInputHeight = 44.dp

/**
 * 검색어 입력. 테두리와 포커스 표시가 없고, 값이 있으면 지우기 버튼이 보인다.
 * `PushlogField` 안에 놓아도 field 상태를 따르지 않는다. 비활성은 [enabled]로만 준다.
 * 검색 키를 누르면 [onSubmit]에 현재 값을 넘기고, [clearFocusOnSubmit]이면 포커스를 풀어 키보드를 내린다.
 * 값이 비어 있어도 [onSubmit]을 부른다. 지우기는 값만 비우고 포커스는 건드리지 않는다.
 */
@Composable
fun PushlogSearchInput(
    state: TextFieldState,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    contentDescription: String = stringResource(R.string.designsystem_search),
    clearButtonLabel: String = stringResource(R.string.designsystem_clear),
    clearFocusOnSubmit: Boolean = true,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    val focusManager = LocalFocusManager.current
    val typography = PushlogTheme.typography
    val color = PushlogTheme.color
    val hasValue by remember(state) { derivedStateOf { state.text.isNotEmpty() } }
    val valueColor = if (enabled) color.fg.neutral else color.fg.disabled
    val placeholderColor = if (enabled) color.fg.placeholder else color.fg.disabled
    ResetPushlogFieldState {
        Row(
            modifier =
                modifier
                    .height(SearchInputHeight)
                    .clip(RoundedCornerShape(PushlogTheme.radius.r2Half))
                    .background(color.bg.neutralWeak)
                    .padding(start = PushlogTheme.dimension.x4, end = PushlogTheme.dimension.x1),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                state = state,
                modifier = Modifier.weight(1f).semantics { this.contentDescription = contentDescription },
                enabled = enabled,
                lineLimits = TextFieldLineLimits.SingleLine,
                textStyle = typography.t6Regular.copy(color = valueColor),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onKeyboardAction = {
                    onSubmit(state.text.toString())
                    if (clearFocusOnSubmit) focusManager.clearFocus()
                },
                interactionSource = interactionSource,
                cursorBrush = SolidColor(color.fg.neutral),
                decorator = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (!hasValue && placeholder != null) {
                            Text(
                                text = placeholder,
                                style = typography.t6Regular,
                                color = placeholderColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            if (hasValue && enabled) {
                ClearButton(
                    onClick = { state.clearText() },
                    contentDescription = clearButtonLabel,
                    iconSize = PushlogTheme.dimension.x5,
                    color = color.fg.neutralSubtle,
                    touchPadding = PushlogTheme.dimension.x3,
                )
            }
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogSearchInputEmptyPreview() {
    PushlogPreviewTheme {
        PushlogSearchInput(state = remember { TextFieldState() }, onSubmit = {}, placeholder = "검색")
    }
}

@PushlogPreview
@Composable
private fun PushlogSearchInputValuePreview() {
    PushlogPreviewTheme { PushlogSearchInput(state = remember { TextFieldState("커피") }, onSubmit = {}) }
}

@PushlogPreview
@Composable
private fun PushlogSearchInputDisabledPreview() {
    PushlogPreviewTheme {
        PushlogSearchInput(state = remember { TextFieldState() }, onSubmit = {}, placeholder = "검색", enabled = false)
    }
}
