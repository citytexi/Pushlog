package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import kotlin.math.roundToInt

// SEED select-box.yaml footer expandHeightDuration·collapseOpacityDuration 400ms. 같은 값의 토큰이 없다.
private const val FOOTER_SLOW_DURATION_MILLIS = 400

/**
 * 카드 아래에서 높이와 투명도로 열고 닫는 footer. 열 때는 높이가 느리고, 닫을 때는 투명도가 느리다.
 *
 * 다 닫히면 내용이 컴포지션에서 빠져 스크린 리더가 숨은 footer를 읽지 않는다.
 * 처음 컴포즈될 때는 애니메이션 없이 끝 상태로 그린다.
 */
@Composable
internal fun SelectBoxFooter(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    val transition = updateTransition(visible, label = "SelectBoxFooter")
    val motion = PushlogTheme.motion
    val easing = motion.timingFunction.easing
    val fastMillis = motion.duration.d6
    val heightFraction =
        transition.animateFloat(
            transitionSpec = { tween(if (targetState) FOOTER_SLOW_DURATION_MILLIS else fastMillis, easing = easing) },
            label = "SelectBoxFooterHeight",
        ) { if (it) 1f else 0f }
    val alpha =
        transition.animateFloat(
            transitionSpec = { tween(if (targetState) fastMillis else FOOTER_SLOW_DURATION_MILLIS, easing = easing) },
            label = "SelectBoxFooterAlpha",
        ) { if (it) 1f else 0f }
    // 목표가 바뀐 첫 프레임에는 isRunning이 아직 false라 currentState도 본다. 그래야 닫기 시작할 때 한 프레임 사라지지 않는다.
    if (!visible && !transition.currentState && !transition.isRunning) return
    Layout(
        content = { Box(Modifier.graphicsLayer { this.alpha = alpha.value }) { content() } },
        modifier = Modifier.clipToBounds(),
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints.copy(minHeight = 0))
        // 비율은 측정 단계에서 읽어 애니메이션 중 재구성 없이 다시 재기만 한다.
        layout(placeable.width, footerHeight(placeable.height, heightFraction.value)) {
            placeable.place(0, 0)
        }
    }
}

private fun footerHeight(
    placeableHeight: Int,
    fraction: Float,
): Int = (placeableHeight * fraction).roundToInt()
