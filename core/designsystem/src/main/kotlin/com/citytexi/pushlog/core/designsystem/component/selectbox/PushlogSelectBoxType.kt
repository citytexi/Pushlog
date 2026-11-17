package com.citytexi.pushlog.core.designsystem.component.selectbox

import com.citytexi.pushlog.core.designsystem.component.pressable.PressableSelectionMode

/** 선택 방식. 마크와 접근성 역할이 따라 바뀐다. */
enum class PushlogSelectBoxType {
    /** 하나만 고른다. 라디오 마크, Role.RadioButton. */
    SINGLE,

    /** 여러 개를 고른다. 체크 마크, Role.Checkbox. */
    MULTIPLE,
    ;

    internal fun selectionMode(): PressableSelectionMode =
        when (this) {
            SINGLE -> PressableSelectionMode.SINGLE
            MULTIPLE -> PressableSelectionMode.TOGGLE
        }
}
