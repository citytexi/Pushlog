package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState

// 시각 효과 없는 탭 영역이다. 눌림 표시는 같은 interactionSource를 받은 pushlogPressIndication이 마크에만 그린다.

/** 체크박스 줄 전체의 탭 영역. */
internal fun Modifier.pushlogTriStateToggleArea(
    state: ToggleableState,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onClick: () -> Unit,
): Modifier =
    triStateToggleable(
        state = state,
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = Role.Checkbox,
        onClick = onClick,
    )

/** 라디오 줄 전체의 탭 영역. */
internal fun Modifier.pushlogSelectableArea(
    selected: Boolean,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    onClick: () -> Unit,
): Modifier =
    selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = Role.RadioButton,
        onClick = onClick,
    )

/** 켬·끔 줄 전체의 탭 영역. [role]은 호출하는 쪽이 정한다. */
internal fun Modifier.pushlogToggleableArea(
    value: Boolean,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
    role: Role,
    onValueChange: (Boolean) -> Unit,
): Modifier =
    toggleable(
        value = value,
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onValueChange = onValueChange,
    )
