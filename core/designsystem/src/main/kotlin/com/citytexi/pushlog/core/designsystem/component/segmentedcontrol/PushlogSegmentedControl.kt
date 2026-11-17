package com.citytexi.pushlog.core.designsystem.component.segmentedcontrol

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PushlogPreview
import kotlin.math.roundToInt

// SEED segmented-control-item.yaml: minWidth 86px, minHeight 34px. 같은 크기의 토큰이 없다.
// 최소 너비는 자연 너비 계산에만 쓴다. 항목에 걸면 좁은 컨테이너에서 칸을 넘친다(SEED max-width: 100%).
private val ItemMinWidth = 86.dp
private val ItemMinHeight = 34.dp

// SEED 사용 지침: 5개 이상 금지. 2개 미만은 고를 것이 없다.
private val OptionCountRange = 2..4

/**
 * 한 화면 안에서 보기를 바꾸는 세그먼트 컨트롤. 화면 이동에는 쓰지 않는다(Tabs). 화면당 하나만 둔다.
 *
 * 항목은 2~4개다. 모든 칸이 같은 너비·높이다. 너비는 가장 넓은 라벨(최소 86dp) 기준이고,
 * 컨테이너가 좁으면 칸이 줄고 라벨이 줄바꿈된다. `Modifier.fillMaxWidth()`로 넓히면 칸과 indicator가 함께 넓어진다.
 * 이미 선택된 항목을 다시 눌러도 [onSelectedIndexChange]를 부르지 않는다.
 */
@Composable
fun PushlogSegmentedControl(
    options: List<PushlogSegmentedControlOption>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(options.size in OptionCountRange) { "PushlogSegmentedControl needs 2 to 4 options, got ${options.size}" }
    require(selectedIndex in options.indices) { "selectedIndex $selectedIndex is out of 0..${options.lastIndex}" }
    val motion = PushlogTheme.motion
    val sources = remember(options.size) { List(options.size) { MutableInteractionSource() } }
    // 첫 그림부터 선택 칸에 있게 시작값을 selectedIndex로 둔다. 이동 중 바뀌면 지금 자리에서 새 칸으로 간다.
    val position = remember { Animatable(selectedIndex.toFloat()) }
    LaunchedEffect(selectedIndex) {
        position.animateTo(selectedIndex.toFloat(), tween(motion.duration.d4, easing = motion.timingFunction.easing))
    }
    val shape = PushlogTheme.radius.full
    Layout(
        content = {
            SegmentedControlIndicator()
            options.forEachIndexed { index, option ->
                SegmentedControlItem(
                    label = option.label,
                    selected = index == selectedIndex,
                    enabled = enabled && option.enabled,
                    interactionSource = sources[index],
                    onClick = { if (index != selectedIndex) onSelectedIndexChange(index) },
                )
            }
        },
        modifier =
            modifier
                .selectableGroup()
                .clip(shape)
                .background(PushlogTheme.color.bg.neutralWeakAlpha)
                .padding(PushlogTheme.dimension.x1),
    ) { measurables, constraints ->
        val indicator = measurables.first()
        val items = measurables.drop(1)
        val count = items.size
        val natural = maxOf(items.maxOf { it.maxIntrinsicWidth(Constraints.Infinity) }, ItemMinWidth.roundToPx())
        val width = (natural * count).coerceIn(constraints.minWidth, constraints.maxWidth)

        // 나머지 픽셀을 칸마다 고루 나눈다.
        fun cellStart(index: Int) = width * index / count
        val cell = width / count
        val height =
            maxOf(items.maxOf { it.maxIntrinsicHeight(cell) }, ItemMinHeight.roundToPx())
                .coerceIn(constraints.minHeight, constraints.maxHeight)
        val itemPlaceables =
            items.mapIndexed { index, item ->
                item.measure(Constraints.fixed(cellStart(index + 1) - cellStart(index), height))
            }
        val indicatorPlaceable = indicator.measure(Constraints.fixed(cell, height))
        layout(width, height) {
            // 위치를 배치 단계에서만 읽어 이동 중에는 다시 구성하지 않고 다시 배치만 한다.
            // placeRelative라 RTL에서는 칸과 indicator가 함께 뒤집힌다.
            indicatorPlaceable.placeRelative((position.value * width / count).roundToInt(), 0)
            itemPlaceables.forEachIndexed { index, placeable -> placeable.placeRelative(cellStart(index), 0) }
        }
    }
}

@PushlogPreview
@Composable
private fun PushlogSegmentedControlPreview() {
    PushlogPreviewTheme {
        var selected by remember { mutableIntStateOf(0) }
        PushlogSegmentedControl(
            options = listOf("전체", "안 읽음", "중요").map { PushlogSegmentedControlOption(it) },
            selectedIndex = selected,
            onSelectedIndexChange = { selected = it },
            modifier = Modifier.padding(PushlogTheme.dimension.x4),
        )
    }
}
