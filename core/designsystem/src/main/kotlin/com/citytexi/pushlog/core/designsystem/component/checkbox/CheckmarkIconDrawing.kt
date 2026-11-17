package com.citytexi.pushlog.core.designsystem.component.checkbox

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser

// 경로는 SEED 체크박스 스니펫이 쓰는 @karrotmarket/react-monochrome-icon 1.35.0(Apache-2.0)에서 옮겼다.
// 24x24 viewBox 기준의 채운 모양이다. 출처 고지는 licenses/seed-design-NOTICE에 있다.

// IconCheckmarkFatFill
private const val CHECK_PATH =
    "M 22.552 3.164 C 21.814 2.583 20.745 2.711 20.164 3.448 L 9.563 16.912 L 3.727 10.823 " +
        "C 3.078 10.146 2.001 10.123 1.324 10.773 C 0.646 11.422 0.623 12.498 1.273 13.176 L 8.462 20.676 " +
        "C 8.803 21.032 9.28 21.222 9.772 21.198 C 10.263 21.174 10.72 20.938 11.025 20.552 L 22.836 5.552 " +
        "C 23.416 4.814 23.289 3.745 22.552 3.164 Z"

// IconMinusFatFill
private const val DASH_PATH =
    "M 4 10.3 C 3.061 10.3 2.3 11.061 2.3 12 C 2.3 12.939 3.061 13.7 4 13.7 H 20 " +
        "C 20.939 13.7 21.7 12.939 21.7 12 C 21.7 11.061 20.939 10.3 20 10.3 H 4 Z"

private const val VIEWBOX = 24f

private val CheckPath = PathParser().parsePathString(CHECK_PATH).toPath()

private val DashPath = PathParser().parsePathString(DASH_PATH).toPath()

/** 그리는 영역을 24x24 viewBox로 보고 체크 모양을 채워 그린다. */
internal fun DrawScope.drawCheckIcon(color: Color) {
    scale(size.width / VIEWBOX, size.height / VIEWBOX, pivot = Offset.Zero) { drawPath(CheckPath, color) }
}

/** 그리는 영역을 24x24 viewBox로 보고 일부 선택을 뜻하는 가로 막대를 채워 그린다. */
internal fun DrawScope.drawDashIcon(color: Color) {
    scale(size.width / VIEWBOX, size.height / VIEWBOX, pivot = Offset.Zero) { drawPath(DashPath, color) }
}
