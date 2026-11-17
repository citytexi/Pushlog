package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

/** 세그먼트 항목 하나. [enabled]가 false거나 컨트롤 전체가 비활성이면 이 항목을 고를 수 없다. */
data class PushlogSegmentedControlOption(
    val label: String,
    val enabled: Boolean = true,
)
