package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

/** 카드 안 배치. SEED `layout`. */
enum class PushlogSelectBoxLayout {
    /** 앞 아이콘·글자를 가로로 놓는다. 한 열에 쓴다. */
    HORIZONTAL,

    /** 앞 아이콘 아래에 글자를 놓는다. 여러 열에 쓴다. */
    VERTICAL,
    ;

    @Composable
    internal fun metrics(): SelectBoxMetrics {
        val dimension = PushlogTheme.dimension
        return when (this) {
            HORIZONTAL -> SelectBoxMetrics(dimension.x5, dimension.x4, dimension.x4, dimension.x3)
            VERTICAL -> SelectBoxMetrics(dimension.x4, dimension.x4, dimension.x5, dimension.x2Half)
        }
    }
}

/** 그룹이 박스에 주는 layout 기본값. 그룹 밖이면 HORIZONTAL. */
internal val LocalSelectBoxLayout = staticCompositionLocalOf { PushlogSelectBoxLayout.HORIZONTAL }
