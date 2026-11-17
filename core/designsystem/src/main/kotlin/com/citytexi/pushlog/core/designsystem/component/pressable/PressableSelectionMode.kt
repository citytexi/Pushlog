package com.citytexi.pushlog.core.designsystem.component.pressable

/** [PushlogPressableSurface]의 selected가 null이 아닐 때 쓰는 선택 방식. */
internal enum class PressableSelectionMode {
    /** toggleable, Role.Checkbox. 칩, 다중 선택 select-box. */
    TOGGLE,

    /** selectable, Role.RadioButton. 세그먼트 항목, 단일 선택 select-box. */
    SINGLE,
}
