package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import com.citytexi.pushlog.core.designsystem.theme.PushlogPreviewTheme
import com.citytexi.pushlog.core.designsystem.theme.PushlogTheme
import com.citytexi.pushlog.core.ui.preview.PreviewSampleIcons
import com.citytexi.pushlog.core.ui.preview.PushlogPreview

// SEED select-box 사용 지침: 열은 최대 3개.
private val ColumnCountRange = 1..3

/**
 * [PushlogSelectBox]를 [columns]개 열의 격자로 놓는 그룹. 상태를 갖지 않는다.
 *
 * 열이 2개 이상이면 박스 기본 layout이 세로이고, 같은 줄의 박스는 가장 큰 박스 높이로 맞춘다.
 * 박스의 `layout` 인자가 그룹 기본값을 덮어쓴다.
 * 너비 제한이 없는 곳(가로 스크롤)에는 둘 수 없다. 그룹 이름은 감싸는 field가 붙인다.
 */
@Composable
fun PushlogSelectBoxGroup(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    content: @Composable () -> Unit,
) {
    require(columns in ColumnCountRange) { "PushlogSelectBoxGroup needs 1 to 3 columns, got $columns" }
    val density = LocalDensity.current
    val dimension = PushlogTheme.dimension
    val gapXPx = with(density) { dimension.x3.roundToPx() }
    val gapYPx = with(density) { dimension.spacingY.componentDefault.roundToPx() }
    val policy = remember(columns, gapXPx, gapYPx) { selectBoxGridPolicy(columns, gapXPx, gapYPx) }
    val multiColumn = columns >= 2
    CompositionLocalProvider(
        LocalSelectBoxLayout provides if (multiColumn) PushlogSelectBoxLayout.VERTICAL else PushlogSelectBoxLayout.HORIZONTAL,
        LocalSelectBoxFillsRow provides multiColumn,
    ) {
        Layout(content = content, modifier = modifier.fillMaxWidth().selectableGroup(), measurePolicy = policy)
    }
}

// 줄 높이를 맞추려면 재기 전에 높이를 알아야 하는데 자식은 한 번만 잴 수 있어 intrinsic 높이로 먼저 정한다.
private fun selectBoxGridPolicy(
    columns: Int,
    gapX: Int,
    gapY: Int,
) = MeasurePolicy { measurables, constraints ->
    require(constraints.hasBoundedWidth) { "PushlogSelectBoxGroup needs a bounded width" }
    val width = constraints.maxWidth
    val cellWidth = ((width - gapX * (columns - 1)) / columns).coerceAtLeast(0)
    val rows = measurables.chunked(columns).map { row -> measureRow(row, columns, cellWidth) }
    val rowHeights = rows.map { row -> row.maxOf { it.height } }
    val height = rowHeights.sum() + gapY * (rows.size - 1).coerceAtLeast(0)
    layout(width, constraints.constrainHeight(height)) {
        var y = 0
        rows.forEachIndexed { r, row ->
            row.forEachIndexed { c, placeable -> placeable.placeRelative(c * (cellWidth + gapX), y) }
            y += rowHeights[r] + gapY
        }
    }
}

private fun measureRow(
    row: List<Measurable>,
    columns: Int,
    cellWidth: Int,
): List<Placeable> {
    if (columns == 1) return row.map { it.measure(Constraints.fixedWidth(cellWidth)) }
    val rowHeight = row.maxOf { it.maxIntrinsicHeight(cellWidth) }
    return row.map { it.measure(Constraints.fixed(cellWidth, rowHeight)) }
}

@PushlogPreview
@Composable
private fun PushlogSelectBoxGroupPreview() {
    PushlogPreviewTheme {
        var selected by remember { mutableIntStateOf(0) }
        val icon = rememberVectorPainter(PreviewSampleIcons.Plus)
        PushlogSelectBoxGroup(modifier = Modifier.padding(PushlogTheme.dimension.x4), columns = 2) {
            listOf("아침", "점심", "저녁", "새벽").forEachIndexed { index, label ->
                PushlogSelectBox(
                    selected = selected == index,
                    onClick = { selected = index },
                    type = PushlogSelectBoxType.SINGLE,
                    label = label,
                    description = if (index == 0) "하루를 여는 알림" else null,
                    prefixIcon = icon,
                )
            }
        }
    }
}
