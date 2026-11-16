package com.citytexi.pushlog.core.designsystem.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import com.citytexi.pushlog.core.designsystem.R
import com.citytexi.pushlog.core.designsystem.component.loading.PushlogProgressCircle
import com.citytexi.pushlog.core.designsystem.theme.LocalPushlogDarkTheme

/** 눌림 부품에 넘길 평상시 표면색. 비활성은 눌려도 같은 색이고, 로딩은 눌림 색을 쓴다. */
internal fun buttonSurfaceColor(
    enabled: Boolean,
    loading: Boolean,
    normal: Color,
    pressed: Color,
    disabled: Color,
): Color =
    when {
        !enabled -> disabled
        loading -> pressed
        else -> normal
    }

/** 눌림 부품에 넘길 눌림 표면색. 비활성이면 [disabled]라 색이 변하지 않는다. */
internal fun buttonPressedColor(
    enabled: Boolean,
    pressed: Color,
    disabled: Color,
): Color = if (enabled) pressed else disabled

/** 로딩 중이면 읽는 이에게 "불러오는 중"을 알린다. 비활성으로는 표시하지 않는다. */
@Composable
internal fun Modifier.pushlogLoadingState(loading: Boolean): Modifier {
    val description = stringResource(R.string.designsystem_loading)
    return semantics { if (loading) stateDescription = description }
}

/** 로딩 중에도 [content]를 투명하게 배치에 남겨 너비를 유지하고, 그 위 가운데에 진행 원을 그린다. */
@Composable
internal fun PushlogLoadingContent(
    loading: Boolean,
    progressSize: Dp,
    trackColor: Color,
    rangeColor: Color,
    content: @Composable () -> Unit,
) {
    Box(contentAlignment = Alignment.Center) {
        Box(Modifier.alpha(if (loading) 0f else 1f)) { content() }
        if (loading) {
            PushlogProgressCircle(size = progressSize, trackColor = trackColor, rangeColor = rangeColor)
        }
    }
}

/** 팔레트 색은 테마를 따라가지 않으므로 라이트·다크 값을 받아 현재 테마에 맞는 쪽을 고른다. */
@Composable
internal fun pushlogThemedColor(
    light: Color,
    dark: Color,
): Color = if (LocalPushlogDarkTheme.current) dark else light
