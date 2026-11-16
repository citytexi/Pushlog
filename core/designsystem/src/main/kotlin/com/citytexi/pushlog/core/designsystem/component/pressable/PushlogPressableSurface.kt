package com.citytexi.pushlog.core.designsystem.component.pressable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.citytexi.pushlog.core.designsystem.R

/**
 * 동작 컴포넌트의 공용 표면. 눌림 색·축소, 클릭 또는 토글, 테두리, 그림자, 로딩 의미 정보를 맡고 [content]를 가운데에 둔다.
 *
 * [selected]가 null이면 버튼이고, 아니면 토글이다. 토글에는 로딩이 없다.
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
    border: BorderStroke? = null,
    shadow: Modifier = Modifier,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    require(selected == null || !loading) { "toggle surfaces have no loading state" }
    val source = interactionSource ?: remember { MutableInteractionSource() }
    val loadingDescription = stringResource(R.string.designsystem_loading)
    val surface =
        if (selected == null) {
            Modifier.pushlogPressable(source, color, pressedColor, shape, enabled, loading, onClick, shadow = shadow)
        } else {
            Modifier.pushlogToggleablePressable(source, selected, color, pressedColor, shape, enabled, onClick, shadow = shadow)
        }
    Box(
        modifier =
            modifier
                .then(surface)
                .then(if (border != null) Modifier.border(border, shape) else Modifier)
                .semantics { if (loading) stateDescription = loadingDescription },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
