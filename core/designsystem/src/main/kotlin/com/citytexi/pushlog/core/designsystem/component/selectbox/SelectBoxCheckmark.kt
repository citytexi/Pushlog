package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.component.checkbox.drawCheckIcon
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme

// SEED select-box-checkmark.yaml icon.size 15px. 같은 크기의 토큰이 없다.
private val IconSize = 15.dp

/**
 * SEED select-box 전용 체크 마크. 탭과 축소를 맡지 않는다(카드가 내용 축소로 함께 줄인다).
 *
 * 색 우선순위는 비활성, 선택, 눌림, 기본 순이다. 눌림은 [interactionSource]에서 읽는다.
 */
@Composable
internal fun SelectBoxCheckmark(
    selected: Boolean,
    enabled: Boolean,
    interactionSource: InteractionSource,
    modifier: Modifier = Modifier,
) {
    val pressed by interactionSource.collectIsPressedAsState()
    val target = iconColor(selected = selected, enabled = enabled, pressed = pressed)
    val color by animateColorAsState(
        targetValue = target,
        animationSpec =
            tween(
                PushlogTheme.motion.duration.colorTransition,
                easing = PushlogTheme.motion.timingFunction.easing,
            ),
        label = "SelectBoxCheckmarkColor",
    )
    Box(modifier.size(PushlogTheme.dimension.x5), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(IconSize)) { drawCheckIcon(color) }
    }
}

@Composable
private fun iconColor(
    selected: Boolean,
    enabled: Boolean,
    pressed: Boolean,
): Color {
    val fg = PushlogTheme.color.fg
    return when {
        !enabled -> fg.disabled
        selected -> fg.neutral
        pressed -> fg.neutralSubtle
        else -> fg.placeholder
    }
}
