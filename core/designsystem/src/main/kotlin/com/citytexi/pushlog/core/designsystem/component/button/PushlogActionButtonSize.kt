package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED action-button.yaml: LARGE 아이콘 크기. 같은 크기의 간격 토큰이 없다.
private val LargeIconSize = 22.dp

enum class PushlogActionButtonSize {
    SMALL,
    MEDIUM,
    LARGE,
    ;

    @Composable
    internal fun metrics(): ActionButtonMetrics {
        val dimension = PushlogTheme.dimension
        val radius = PushlogTheme.radius
        val typography = PushlogTheme.typography
        return when (this) {
            SMALL ->
                ActionButtonMetrics(
                    minHeight = dimension.x9,
                    cornerRadius = radius.r2,
                    paddingX = dimension.x3Half,
                    paddingY = dimension.x2,
                    gap = dimension.x1,
                    prefixIconSize = dimension.x3Half,
                    labelStyle = typography.t4Bold,
                    progressSize = dimension.x3Half,
                    iconOnlyPadding = dimension.x2,
                    iconOnlyIconSize = dimension.x4,
                )
            MEDIUM ->
                ActionButtonMetrics(
                    minHeight = dimension.x10,
                    cornerRadius = radius.r2,
                    paddingX = dimension.x4,
                    paddingY = dimension.x2Half,
                    gap = dimension.x1Half,
                    prefixIconSize = dimension.x4,
                    labelStyle = typography.t4Bold,
                    progressSize = dimension.x4,
                    iconOnlyPadding = dimension.x2Half,
                    iconOnlyIconSize = dimension.x4Half,
                )
            LARGE ->
                ActionButtonMetrics(
                    minHeight = dimension.x13,
                    cornerRadius = radius.r3,
                    paddingX = dimension.x5,
                    paddingY = dimension.x3Half,
                    gap = dimension.x2,
                    prefixIconSize = LargeIconSize,
                    labelStyle = typography.t6Bold,
                    progressSize = dimension.x4Half,
                    iconOnlyPadding = dimension.x3Half,
                    iconOnlyIconSize = LargeIconSize,
                )
        }
    }
}
