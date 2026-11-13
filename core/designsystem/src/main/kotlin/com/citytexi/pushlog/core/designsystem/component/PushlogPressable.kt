package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import kotlin.math.max

private const val PRESSED_SHRINK_DP = 2f
private const val MIN_REFERENCE_DP = 24f
private const val WIDTH_DIVISOR = 4f

/** 눌리면 줄어드는 배율. 기준 길이는 높이, 너비÷4, 24dp 중 가장 큰 값이다. */
internal fun pressedScaleFactor(
    widthPx: Float,
    heightPx: Float,
    density: Float,
): Float {
    val reference = max(heightPx, max(widthPx / WIDTH_DIVISOR, MIN_REFERENCE_DP * density))
    return (reference - PRESSED_SHRINK_DP * density) / reference
}

/** 눌리면 배경이 [pressedColor]로 바뀌고 [pressedScaleFactor]만큼 줄어든다. ripple은 쓰지 않는다. */
internal fun Modifier.pushlogPressable(
    interactionSource: MutableInteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    role: Role = Role.Button,
): Modifier =
    pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading) {
        clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = role,
            onClick = { if (!loading) onClick() },
        )
    }

/** [pushlogPressable]과 같되 선택 상태를 가진 칩용이다. */
internal fun Modifier.pushlogToggleablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
): Modifier =
    pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading) {
        toggleable(
            value = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = { if (!loading) onClick() },
        )
    }

// graphicsLayer는 clickable 앞에 둬서 눌림 영역도 같이 줄어든다.
// composed는 모션 토큰과 눌림 상태를 읽는 Composable 컨텍스트가 필요해서 쓴다.
private fun Modifier.pressableSurface(
    interactionSource: MutableInteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    gesture: Modifier.() -> Modifier,
): Modifier =
    composed {
        val motion = PushlogTheme.motion
        val pressed by interactionSource.collectIsPressedAsState()
        val reduceMotion = rememberReduceMotion()
        val showPressedColor = loading || (enabled && pressed)
        val shrinks = enabled && !loading && !reduceMotion && pressed
        val background by animateColorAsState(
            targetValue = if (showPressedColor) pressedColor else color,
            animationSpec = tween(motion.duration.colorTransition, easing = motion.timingFunction.easing),
            label = "pushlogPressableColor",
        )
        val shrink by animateFloatAsState(
            targetValue = if (shrinks) 1f else 0f,
            animationSpec = tween(motion.duration.pressedScale, easing = motion.timingFunction.pressedScale),
            label = "pushlogPressableScale",
        )
        Modifier
            .graphicsLayer {
                val target = pressedScaleFactor(size.width, size.height, density)
                val scale = 1f + (target - 1f) * shrink
                scaleX = scale
                scaleY = scale
            }.clip(shape)
            .background(background)
            .gesture()
    }
