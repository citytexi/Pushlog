package com.citytexi.pushlog.core.designsystem.component.field

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser

// 경로는 @karrotmarket/react-monochrome-icon 1.35.0(Apache-2.0)의 IconExclamationmarkCircleFill에서 옮겼다.
// 24x24 viewBox 기준의 채운 모양이다. 출처 고지는 licenses/seed-design-NOTICE에 있다.
private const val EXCLAMATION_PATH =
    "M 12 1 C 18.075 1 23 5.925 23 12 C 23 18.075 18.075 23 12 23 C 5.925 23 1 18.075 1 12 " +
        "C 1 5.925 5.925 1 12 1 Z M 12 15.183 C 11.313 15.183 10.75 15.739 10.75 16.433 " +
        "C 10.75 17.126 11.313 17.683 12 17.683 C 12.687 17.683 13.25 17.126 13.25 16.433 " +
        "C 13.25 15.739 12.687 15.183 12 15.183 Z M 12 5.93 C 11.448 5.93 11 6.378 11 6.931 " +
        "V 12.431 C 11 12.983 11.448 13.431 12 13.431 C 12.552 13.431 13 12.983 13 12.431 " +
        "V 6.931 C 13 6.378 12.552 5.93 12 5.93 Z"

private const val VIEWBOX = 24f

// 원 안의 느낌표를 구멍으로 뚫는 모양이라 EvenOdd로 채운다.
private val ExclamationPath =
    PathParser().parsePathString(EXCLAMATION_PATH).toPath().apply { fillType = PathFillType.EvenOdd }

/** 그리는 영역을 24x24 viewBox로 보고 느낌표 원을 채워 그린다. */
internal fun DrawScope.drawExclamationCircleIcon(color: Color) {
    scale(size.width / VIEWBOX, size.height / VIEWBOX, pivot = Offset.Zero) { drawPath(ExclamationPath, color) }
}
