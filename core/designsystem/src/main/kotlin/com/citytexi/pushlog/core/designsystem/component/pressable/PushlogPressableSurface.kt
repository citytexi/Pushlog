package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.citytexi.pushlog.core.designsystem.R

/**
 * 동작 컴포넌트의 공용 표면. 눌림 색·축소, 클릭 또는 토글, 테두리, 그림자, 로딩 의미 정보를 맡고 [content]를 가운데에 둔다.
 *
 * [selected]가 null이면 버튼이고, 아니면 [selectionMode]에 따라 토글 또는 단일 선택이다. 선택 표면에는 로딩이 없다.
 * [scaleScope]는 눌렸을 때 표면 전체가 줄지, 표면은 두고 [content]만 줄지 정한다.
 * [pressedBorder]는 활성 상태에서 눌린 동안만 [border] 위에 그려진다.
 * 크기와 접근성 이름은 [modifier]로 넘긴다. 로딩 중 진행 원은 [content]가 그린다.
 */
@Composable
internal fun PushlogPressableSurface(
    color: Color,
    pressedColor: Color,
    shape: Shape,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    selected: Boolean? = null,
    selectionMode: PressableSelectionMode = PressableSelectionMode.TOGGLE,
    scaleScope: PressScaleScope = PressScaleScope.SELF,
    border: BorderStroke? = null,
    pressedBorder: BorderStroke? = null,
    shadow: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    require(selected == null || !loading) { "selection surfaces have no loading state" }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val loadingDescription = stringResource(R.string.designsystem_loading)
    val scaleSelf = scaleScope == PressScaleScope.SELF
    val surface =
        when {
            selected == null ->
                Modifier.pushlogPressable(
                    source,
                    color,
                    pressedColor,
                    shape,
                    enabled,
                    loading,
                    onClick,
                    shadow = shadow,
                    scaleSelf = scaleSelf,
                )
            selectionMode == PressableSelectionMode.TOGGLE ->
                Modifier.pushlogToggleablePressable(
                    source,
                    selected,
                    color,
                    pressedColor,
                    shape,
                    enabled,
                    onClick,
                    shadow = shadow,
                    scaleSelf = scaleSelf,
                )
            else ->
                Modifier.pushlogSelectablePressable(
                    source,
                    selected,
                    color,
                    pressedColor,
                    shape,
                    enabled,
                    onClick,
                    shadow = shadow,
                    scaleSelf = scaleSelf,
                )
        }
    val surfaceModifier =
        modifier
            .then(surface)
            .then(if (border != null) Modifier.border(border, shape) else Modifier)
            .then(if (pressedBorder != null) pressedBorderModifier(pressedBorder, source, enabled, shape) else Modifier)
            .semantics { if (loading) stateDescription = loadingDescription }
    if (scaleSelf) {
        Box(modifier = surfaceModifier, contentAlignment = Alignment.Center) {
            content()
        }
    } else {
        ContentScaledBox(surfaceModifier, source, enabled, loading, content)
    }
}

// 안쪽 상자가 표면의 최소 제약을 그대로 받아 표면과 크기가 같다. 그래서 PressScaleNode가 자기 크기로 잰 배율이 표면 크기 기준이 된다.
@Composable
private fun ContentScaledBox(
    surfaceModifier: Modifier,
    source: MutableInteractionSource,
    enabled: Boolean,
    loading: Boolean,
    content: @Composable () -> Unit,
) {
    Box(modifier = surfaceModifier, contentAlignment = Alignment.Center, propagateMinConstraints = true) {
        Box(Modifier.then(PressScaleNode.Element(source, enabled, loading)), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

// 눌림 여부를 composition에서 읽으므로 pressedBorder가 있는 표면에서만 부른다.
@Composable
private fun pressedBorderModifier(
    pressedBorder: BorderStroke,
    source: MutableInteractionSource,
    enabled: Boolean,
    shape: Shape,
): Modifier {
    val pressed by source.collectIsPressedAsState()
    val brush = if (pressed && enabled) pressedBorder.brush else SolidColor(Color.Transparent)
    return Modifier.border(pressedBorder.width, brush, shape)
}
