package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

/**
 * 눌리면 배경이 [pressedColor]로 바뀌고 [pressedScaleFactor]만큼 줄어든다. ripple은 쓰지 않는다.
 * [shadow]는 clip 바깥에 그려지되 표면과 함께 줄어들 그림자다. 이 modifier 앞에 두면 줄지 않는다.
 */
internal fun Modifier.pushlogPressable(
    interactionSource: MutableInteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
    role: Role = Role.Button,
    shadow: Modifier = Modifier,
): Modifier =
    pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading, shadow)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = role,
            onClick = { if (!loading) onClick() },
        )

/** [pushlogPressable]과 같되 선택 상태를 가진 칩용이다. */
internal fun Modifier.pushlogToggleablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    shadow: Modifier = Modifier,
): Modifier =
    pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading = false, shadow = shadow)
        .toggleable(
            value = selected,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            role = Role.Checkbox,
            onValueChange = { onClick() },
        )

// 축소 층을 맨 앞에 둬서 그림자, clip, 배경, 눌림 영역이 모두 같이 줄어든다.
private fun Modifier.pressableSurface(
    interactionSource: MutableInteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    shadow: Modifier,
): Modifier =
    this
        .then(PressScaleNode.Element(interactionSource, enabled, loading))
        .then(shadow)
        .clip(shape)
        .then(PressColorNode.Element(interactionSource, color, pressedColor, enabled, loading))

/** [source]의 눌림 여부가 바뀔 때마다 [onChange]를 부른다. `collectIsPressedAsState`와 같은 규칙이다. */
internal fun CoroutineScope.launchPressTracking(
    source: InteractionSource,
    onChange: (Boolean) -> Unit,
): Job =
    launch {
        val presses = mutableListOf<PressInteraction.Press>()
        source.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> presses.add(interaction)
                is PressInteraction.Release -> presses.remove(interaction.press)
                is PressInteraction.Cancel -> presses.remove(interaction.press)
            }
            onChange(presses.isNotEmpty())
        }
    }
