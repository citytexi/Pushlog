package com.citytexi.pushlog.core.designsystem.component.selectbox

import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight

/**
 * [PushlogSelectBoxGroup]의 격자 측정.
 *
 * 줄 높이를 맞추려면 재기 전에 높이를 알아야 하는데 자식은 한 번만 잴 수 있어 intrinsic 높이로 먼저 정한다.
 * intrinsic도 직접 계산한다. 기본 구현은 너비 제한 없이 측정을 불러 `require`에 걸린다.
 */
internal class SelectBoxGridMeasurePolicy(
    private val columns: Int,
    private val gapX: Int,
    private val gapY: Int,
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints,
    ): MeasureResult {
        require(constraints.hasBoundedWidth) { "PushlogSelectBoxGroup needs a bounded width" }
        val width = constraints.maxWidth
        val rows = measurables.chunked(columns).map { row -> measureRow(row, width) }
        val rowHeights = rows.map { row -> row.maxOf { it.height } }
        return layout(width, constraints.constrainHeight(stack(rowHeights))) {
            var y = 0
            rows.forEachIndexed { r, row ->
                row.forEachIndexed { c, placeable -> placeable.placeRelative(cellStart(width, c), y) }
                y += rowHeights[r] + gapY
            }
        }
    }

    // 칸은 열 수만큼 늘 있으므로 가장 넓은 자식 × 열 수다.
    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int = gridWidth(measurables.maxOfOrNull { it.minIntrinsicWidth(height) })

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int,
    ): Int = gridWidth(measurables.maxOfOrNull { it.maxIntrinsicWidth(height) })

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = gridHeight(measurables, width) { measurable, cellWidth -> measurable.minIntrinsicHeight(cellWidth) }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
    ): Int = gridHeight(measurables, width) { measurable, cellWidth -> measurable.maxIntrinsicHeight(cellWidth) }

    private fun measureRow(
        row: List<Measurable>,
        width: Int,
    ): List<Placeable> {
        if (columns == 1) return row.map { it.measure(Constraints.fixedWidth(cellWidth(width))) }
        val rowHeight = row.maxOf { it.maxIntrinsicHeight(cellWidth(width)) }
        return row.map { it.measure(Constraints.fixed(cellWidth(width), rowHeight)) }
    }

    private fun gridWidth(widestCell: Int?): Int = if (widestCell == null) 0 else widestCell * columns + gapX * (columns - 1)

    private fun gridHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int,
        cellHeight: (IntrinsicMeasurable, Int) -> Int,
    ): Int =
        stack(
            measurables.chunked(columns).map { row ->
                row.maxOf { cellHeight(it, cellWidth(width)) }
            },
        )

    private fun stack(rowHeights: List<Int>): Int = rowHeights.sum() + gapY * (rowHeights.size - 1).coerceAtLeast(0)

    private fun cellStart(
        width: Int,
        index: Int,
    ): Int = index * (cellWidth(width) + gapX)

    // intrinsic 높이는 너비 제한 없이 물을 수 있다. 그때는 칸 너비도 제한하지 않는다.
    private fun cellWidth(width: Int): Int =
        if (width == Constraints.Infinity) {
            Constraints.Infinity
        } else {
            ((width - gapX * (columns - 1)) / columns).coerceAtLeast(0)
        }
}
