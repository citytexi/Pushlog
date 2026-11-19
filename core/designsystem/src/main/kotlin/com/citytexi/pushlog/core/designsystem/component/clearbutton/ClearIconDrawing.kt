package com.citytexi.pushlog.core.designsystem.component.clearbutton

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser

// 경로는 @karrotmarket/react-monochrome-icon 1.35.0(Apache-2.0)의 IconXmarkCircleFill에서 옮겼다.
// 24x24 viewBox 기준의 채운 모양이다. 출처 고지는 licenses/seed-design-NOTICE에 있다.
private const val XMARK_CIRCLE_PATH =
    "M 0.801 12 C 0.801 5.817 5.818 0.8 12.001 0.8 C 18.183 0.8 23.201 5.817 23.201 12 " +
        "C 23.201 18.183 18.183 23.2 12.001 23.2 C 5.818 23.2 0.801 18.183 0.801 12 Z " +
        "M 8.465 8.466 C 8.856 8.075 9.489 8.075 9.88 8.466 L 12.001 10.587 L 14.122 8.466 " +
        "C 14.512 8.075 15.145 8.075 15.536 8.466 C 15.926 8.856 15.926 9.489 15.536 9.88 " +
        "L 13.415 12.001 L 15.536 14.123 C 15.927 14.513 15.927 15.146 15.536 15.537 " +
        "C 15.146 15.927 14.513 15.927 14.122 15.537 L 12.001 13.415 L 9.879 15.537 " +
        "C 9.488 15.927 8.855 15.927 8.465 15.537 C 8.074 15.146 8.074 14.513 8.465 14.123 " +
        "L 10.586 12.001 L 8.465 9.88 C 8.075 9.489 8.075 8.856 8.465 8.466 Z"

private const val VIEWBOX = 24f

// 원 안의 X를 구멍으로 뚫는 모양이라 EvenOdd로 채운다.
private val XmarkCirclePath =
    PathParser().parsePathString(XMARK_CIRCLE_PATH).toPath().apply { fillType = PathFillType.EvenOdd }

/** 그리는 영역을 24x24 viewBox로 보고 X 원을 채워 그린다. */
internal fun DrawScope.drawXmarkCircleIcon(color: Color) {
    scale(size.width / VIEWBOX, size.height / VIEWBOX, pivot = Offset.Zero) { drawPath(XmarkCirclePath, color) }
}
