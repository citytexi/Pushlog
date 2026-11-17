package com.citytexi.pushlog.core.designsystem.component.radio

import androidx.compose.runtime.Composable
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** 라디오 마크 크기. 라디오 줄의 높이와 라벨 글자 크기도 이 값을 따른다. */
enum class PushlogRadiomarkSize {
    MEDIUM,
    LARGE,
    ;

    @Composable
    internal fun metrics(): RadiomarkMetrics {
        val dimension = PushlogTheme.dimension
        val typography = PushlogTheme.typography
        return when (this) {
            MEDIUM ->
                RadiomarkMetrics(
                    markSize = dimension.x5,
                    dotSize = dimension.x2,
                    disabledDotSize = dimension.x2Half,
                    rowMinHeight = dimension.x8,
                    regularLabelStyle = typography.t4Regular,
                    boldLabelStyle = typography.t4Bold,
                )
            LARGE ->
                RadiomarkMetrics(
                    markSize = dimension.x6,
                    dotSize = dimension.x2Half,
                    disabledDotSize = dimension.x3,
                    rowMinHeight = dimension.x9,
                    regularLabelStyle = typography.t5Regular,
                    boldLabelStyle = typography.t5Bold,
                )
        }
    }
}
