package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.selection.selectable
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
 * [scaleSelf]가 false면 표면은 줄지 않는다. 내용만 줄일 때 쓴다.
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
    scaleSelf: Boolean = true,
): Modifier =
    clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onClick = { if (!loading) onClick() },
    ).pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading, shadow, scaleSelf)

/** [pushlogPressable]과 같되 선택 상태를 가진다. [role] 기본값은 칩의 역할이다. */
internal fun Modifier.pushlogToggleablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    role: Role = Role.Checkbox,
    shadow: Modifier = Modifier,
    scaleSelf: Boolean = true,
): Modifier =
    toggleable(
        value = selected,
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = role,
        onValueChange = { onClick() },
    ).pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading = false, shadow = shadow, scaleSelf = scaleSelf)

/** [pushlogToggleablePressable]과 같되 여럿 중 하나를 고르는 단일 선택이다. 이미 선택된 항목을 눌러도 [onClick]이 불린다. */
internal fun Modifier.pushlogSelectablePressable(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    shadow: Modifier = Modifier,
    scaleSelf: Boolean = true,
): Modifier =
    selectable(
        selected = selected,
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        role = Role.RadioButton,
        onClick = onClick,
    ).pressableSurface(interactionSource, color, pressedColor, shape, enabled, loading = false, shadow = shadow, scaleSelf = scaleSelf)

// 탭 판정은 축소 층 바깥에 둔다. 판정 영역까지 줄면 가장자리를 누른 손가락이 줄어든 표면 밖으로 밀려나 탭이 취소된다.
// 축소 층은 그 안에서 맨 앞이라 그림자, clip, 배경이 모두 같이 줄어든다.
private fun Modifier.pressableSurface(
    interactionSource: MutableInteractionSource,
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    loading: Boolean,
    shadow: Modifier,
    scaleSelf: Boolean,
): Modifier =
    this
        .then(if (scaleSelf) PressScaleNode.Element(interactionSource, enabled, loading) else Modifier)
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
