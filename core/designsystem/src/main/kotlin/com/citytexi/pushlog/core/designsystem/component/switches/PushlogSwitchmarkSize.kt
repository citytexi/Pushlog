package com.citytexi.pushlog.core.designsystem.component.switches

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED switchmark.yaml: size=16·24·32의 트랙 너비. 같은 크기의 토큰이 없다.
private val SmallTrackWidth = 26.dp
private val MediumTrackWidth = 38.dp
private val LargeTrackWidth = 52.dp

// SEED switchmark.yaml: size=32 padding 3px, thumb 26px. 같은 크기의 토큰이 없다.
private val LargePadding = 3.dp
private val LargeThumbSize = 26.dp

/** 스위치 마크 크기. 스위치 줄의 높이·간격과 라벨 글자 크기도 이 값을 따른다. */
enum class PushlogSwitchmarkSize {
    /** 높이 16dp. 줄 높이는 24dp를 지킨다. */
    SMALL,

    /** 높이 24dp. */
    MEDIUM,

    /** 높이 32dp. */
    LARGE,
    ;

    @Composable
    internal fun metrics(): SwitchmarkMetrics {
        val dimension = PushlogTheme.dimension
        val typography = PushlogTheme.typography
        return when (this) {
            SMALL ->
                SwitchmarkMetrics(
                    trackWidth = SmallTrackWidth,
                    trackHeight = dimension.x4,
                    padding = dimension.x0Half,
                    thumbSize = dimension.x3,
                    rowMinHeight = dimension.x6,
                    gap = dimension.x1Half,
                    labelStyle = typography.t3Medium,
                )
            MEDIUM ->
                SwitchmarkMetrics(
                    trackWidth = MediumTrackWidth,
                    trackHeight = dimension.x6,
                    padding = dimension.x0Half,
                    thumbSize = dimension.x5,
                    rowMinHeight = dimension.x6,
                    gap = dimension.x2,
                    labelStyle = typography.t4Medium,
                )
            LARGE ->
                SwitchmarkMetrics(
                    trackWidth = LargeTrackWidth,
                    trackHeight = dimension.x8,
                    padding = LargePadding,
                    thumbSize = LargeThumbSize,
                    rowMinHeight = dimension.x8,
                    gap = dimension.x2Half,
                    labelStyle = typography.t5Medium,
                )
        }
    }
}
