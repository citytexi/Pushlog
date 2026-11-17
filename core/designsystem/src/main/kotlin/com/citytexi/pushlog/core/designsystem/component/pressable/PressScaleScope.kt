package com.citytexi.pushlog.core.designsystem.component.pressable

/** 눌렸을 때 줄어드는 대상. SEED `scaleScope`. */
internal enum class PressScaleScope {
    /** 표면 전체(그림자·배경·테두리·내용)가 준다. */
    SELF,

    /** 표면 색·테두리는 그대로 두고 안의 내용만 준다. 배율은 표면 크기로 계산한다. */
    CONTENT,
}
