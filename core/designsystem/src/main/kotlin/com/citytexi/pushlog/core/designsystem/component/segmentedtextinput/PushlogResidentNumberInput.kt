package com.citytexi.pushlog.core.designsystem.component.segmentedtextinput

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.citytexi.pushlog.core.designsystem.component.field.LocalPushlogFieldState

/**
 * 생년월일 칸과 뒷자리 칸으로 나눈 주민등록번호 입력. 형식은 [PushlogResidentNumberState.digits]가 정한다.
 * 생년월일 칸은 자동완성으로 들어온 yyyy-MM-dd 같은 날짜를 6자리로 바꾼다. 가린 뒷자리는 붙여넣기·자동완성으로 채우지 않는다.
 * `PushlogField` 안에 놓으면 field의 invalid·enabled·readOnly를 기본값으로 따른다.
 */
@Composable
fun PushlogResidentNumberInput(
    state: PushlogResidentNumberState,
    modifier: Modifier = Modifier,
    invalid: Boolean = LocalPushlogFieldState.current.invalid,
    enabled: Boolean = LocalPushlogFieldState.current.enabled,
    readOnly: Boolean = LocalPushlogFieldState.current.readOnly,
) {
    val slots = remember(state.digits) { state.digits.slots }
    SegmentedTextInput(slots, state.fields, invalid, enabled, readOnly, modifier)
}
