package com.citytexi.pushlog.core.designsystem.component.chip

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED chip.yaml: SMALL 최소 너비. 같은 크기의 간격 토큰이 없다.
private val SmallMinWidth = 44.dp

enum class PushlogChipSize {
    SMALL,
    MEDIUM,
    LARGE,
    ;

    @Composable
    internal fun metrics(): ChipMetrics {
        val dimension = PushlogTheme.dimension
        return when (this) {
            SMALL -> ChipMetrics(dimension.x8, dimension.x1Half, dimension.x3Half, dimension.x3Half, SmallMinWidth)
            MEDIUM -> ChipMetrics(dimension.x9, dimension.x2, dimension.x4, dimension.x3Half, dimension.x12)
            LARGE -> ChipMetrics(dimension.x10, dimension.x2Half, dimension.x4, dimension.x4, dimension.x13)
        }
    }
}
