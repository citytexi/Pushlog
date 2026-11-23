package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState

/**
 * 4자리 칸 넷으로 나눈 카드번호 입력. 뒤에서 [PushlogCardNumberState.maskedDigits]만큼 가린다.
 * 첫 칸에 붙여넣거나 자동완성으로 들어온 번호는 가린 칸 앞까지 나눠 채운다. [suffix]는 끝에 붙는 자리(카드사 로고)다.
 * `PushlogField` 안에 놓으면 field의 invalid·enabled·readOnly를 기본값으로 따른다.
 */
@Composable
fun PushlogCardNumberInput(
    state: PushlogCardNumberState,
    modifier: Modifier = Modifier,
    suffix: (@Composable () -> Unit)? = null,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
) {
    val slots = remember(state.maskedDigits) { state.maskedDigits.slots }
    SegmentedTextInput(slots, state.fields, invalid, enabled, readOnly, modifier, suffix)
}
