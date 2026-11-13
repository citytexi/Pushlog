package com.citytexi.pushlog.core.designsystem.component

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// SEED progress-circle.yaml indeterminate=true. 두께는 버튼·플로팅 버튼 정의의 2px.
private val Thickness = 2.dp
private const val CYCLE_MILLIS = 1200
private val HeadEasing = CubicBezierEasing(0.35f, 0f, 0.65f, 1f)
private val TailEasing = CubicBezierEasing(0.35f, 0f, 0.65f, 0.6f)
private val RotateEasing = CubicBezierEasing(0.35f, 0.25f, 0.65f, 0.75f)
private const val FULL_TURN = 360f
private const val START_ANGLE = -90f

/**
 * 끝나는 때를 모르는 로딩에 쓰는 원. 접근성 노드를 만들지 않으니 상태 설명은 쓰는 컴포넌트가 붙인다.
 */
@Composable
internal fun PushlogProgressCircle(
    size: Dp,
    trackColor: Color,
    rangeColor: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "pushlogProgressCircle")
    val head by transition.animateFloat(0f, 1f, cycle(HeadEasing), label = "head")
    val tail by transition.animateFloat(0f, 1f, cycle(TailEasing), label = "tail")
    val rotation by transition.animateFloat(0f, FULL_TURN, cycle(RotateEasing), label = "rotation")
    Canvas(modifier.size(size)) {
        val stroke = Stroke(width = Thickness.toPx(), cap = StrokeCap.Round)
        val inset = stroke.width / 2
        val arcSize = Size(this.size.width - stroke.width, this.size.height - stroke.width)
        drawArc(trackColor, 0f, FULL_TURN, false, Offset(inset, inset), arcSize, style = stroke)
        drawArc(
            color = rangeColor,
            startAngle = START_ANGLE + rotation + tail * FULL_TURN,
            sweepAngle = (head - tail) * FULL_TURN,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = arcSize,
            style = stroke,
        )
    }
}

private fun cycle(easing: CubicBezierEasing) = infiniteRepeatable<Float>(tween(CYCLE_MILLIS, easing = easing), RepeatMode.Restart)
