package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** 체크 마크 크기. 체크박스 줄의 높이와 라벨 글자 크기도 이 값을 따른다. */
enum class PushlogCheckmarkSize {
    MEDIUM,
    LARGE,
    ;

    @Composable
    internal fun metrics(): CheckmarkMetrics {
        val dimension = PushlogTheme.dimension
        val radius = PushlogTheme.radius
        val typography = PushlogTheme.typography
        return when (this) {
            MEDIUM ->
                CheckmarkMetrics(
                    markSize = dimension.x5,
                    cornerRadius = radius.r1,
                    squareIconSize = dimension.x3,
                    ghostIconSize = dimension.x3Half,
                    rowMinHeight = dimension.x8,
                    regularLabelStyle = typography.t4Regular,
                    boldLabelStyle = typography.t4Bold,
                )
            LARGE ->
                CheckmarkMetrics(
                    markSize = dimension.x6,
                    cornerRadius = radius.r1,
                    squareIconSize = dimension.x3Half,
                    ghostIconSize = dimension.x4Half,
                    rowMinHeight = dimension.x9,
                    regularLabelStyle = typography.t5Regular,
                    boldLabelStyle = typography.t5Bold,
                )
        }
    }
}
