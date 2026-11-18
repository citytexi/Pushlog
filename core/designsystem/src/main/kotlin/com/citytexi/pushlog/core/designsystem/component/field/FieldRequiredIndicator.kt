package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED field 머리 필수 표시. 점 지름 6, 위 4, 왼쪽 2. 글자 크기를 따라가도록 sp로 둔다.
private const val DOT_DIAMETER_SP = 6
private const val DOT_TOP_SP = 4
private const val DOT_START_SP = 2

/** 라벨 옆 필수 표시 점. 글꼴 배율에 맞춰 커진다. */
@Composable
internal fun FieldRequiredIndicator() {
    val density = LocalDensity.current
    val diameter = with(density) { DOT_DIAMETER_SP.sp.toDp() }
    val top = with(density) { DOT_TOP_SP.sp.toDp() }
    val start = with(density) { DOT_START_SP.sp.toDp() }
    Box(
        Modifier
            .padding(top = top, start = start)
            .size(diameter)
            .background(PushlogTheme.color.fg.critical, CircleShape),
    )
}
