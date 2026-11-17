package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.button.pushlogThemedColor
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.designsystem.theme.color.DarkPalette
import com.citytexi.pushlog.core.designsystem.theme.color.LightPalette

// SEED segmented-control-indicator.yaml: strokeWidth 1px. 같은 크기의 토큰이 없다.
private val StrokeWidth = 1.dp

/** 선택 칸 뒤에 깔리는 흰 판. 눌림·비활성 색은 선택 항목이 위에 덮어 칠하므로 여기는 늘 같은 색이다. */
@Composable
internal fun SegmentedControlIndicator(modifier: Modifier = Modifier) {
    val shape = PushlogTheme.radius.full
    Box(
        modifier
            .background(pushlogThemedColor(LightPalette.gray00, DarkPalette.gray00), shape)
            .border(StrokeWidth, PushlogTheme.color.stroke.neutralMuted, shape),
    )
}
