package com.citytexi.pushlog.core.designsystem.component.selectbox

/** footer를 언제 보일지. SEED `footerVisibility`. */
enum class PushlogSelectBoxFooterVisibility {
    /** 선택됐을 때만 보인다. */
    WHEN_SELECTED,

    /** 선택과 상관없이 늘 보인다. */
    ALWAYS,

    /** 선택되지 않았을 때만 보인다. */
    WHEN_NOT_SELECTED,
    ;

    internal fun isVisible(selected: Boolean): Boolean =
        when (this) {
            WHEN_SELECTED -> selected
            ALWAYS -> true
            WHEN_NOT_SELECTED -> !selected
        }
}
