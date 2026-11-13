package com.citytexi.pushlog.core.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * 미리보기·테스트·`app-preview` 전용 샘플 아이콘. 제품 아이콘이 아니다. 아이콘 세트는 범위 밖(스펙 §2).
 * 단색 `Color.Black` fill이라 쓰는 쪽에서 tint한다.
 */
object PreviewSampleIcons {
    val Plus: ImageVector =
        icon("Plus") {
            moveTo(11f, 5f)
            horizontalLineTo(13f)
            verticalLineTo(11f)
            horizontalLineTo(19f)
            verticalLineTo(13f)
            horizontalLineTo(13f)
            verticalLineTo(19f)
            horizontalLineTo(11f)
            verticalLineTo(13f)
            horizontalLineTo(5f)
            verticalLineTo(11f)
            horizontalLineTo(11f)
            close()
        }

    val Close: ImageVector =
        icon("Close") {
            moveTo(6.4f, 5f)
            lineTo(12f, 10.6f)
            lineTo(17.6f, 5f)
            lineTo(19f, 6.4f)
            lineTo(13.4f, 12f)
            lineTo(19f, 17.6f)
            lineTo(17.6f, 19f)
            lineTo(12f, 13.4f)
            lineTo(6.4f, 19f)
            lineTo(5f, 17.6f)
            lineTo(10.6f, 12f)
            lineTo(5f, 6.4f)
            close()
        }

    val ChevronDown: ImageVector =
        icon("ChevronDown") {
            moveTo(6.4f, 8.6f)
            lineTo(12f, 14.2f)
            lineTo(17.6f, 8.6f)
            lineTo(19f, 10f)
            lineTo(12f, 17f)
            lineTo(5f, 10f)
            close()
        }

    val ArrowUp: ImageVector =
        icon("ArrowUp") {
            moveTo(11f, 19f)
            verticalLineTo(7.8f)
            lineTo(6.4f, 12.4f)
            lineTo(5f, 11f)
            lineTo(12f, 4f)
            lineTo(19f, 11f)
            lineTo(17.6f, 12.4f)
            lineTo(13f, 7.8f)
            verticalLineTo(19f)
            close()
        }

    private fun icon(
        name: String,
        pathBuilder: PathBuilder.() -> Unit,
    ): ImageVector =
        ImageVector
            .Builder(name, 24.dp, 24.dp, viewportWidth = 24f, viewportHeight = 24f)
            .path(fill = SolidColor(Color.Black), pathBuilder = pathBuilder)
            .build()
}
