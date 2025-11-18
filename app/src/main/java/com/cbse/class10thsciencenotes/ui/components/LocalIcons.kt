package com.cbse.class10thsciencenotes.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ZoomIn: ImageVector
    get() {
        if (_ZoomIn != null) return _ZoomIn!!

        _ZoomIn = ImageVector.Builder(
            name = "ZoomIn",
            defaultWidth = 15.dp,
            defaultHeight = 15.dp,
            viewportWidth = 15f,
            viewportHeight = 15f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(10f, 6.5f)
                curveTo(10f, 8.433f, 8.433f, 10f, 6.5f, 10f)
                curveTo(4.567f, 10f, 3f, 8.433f, 3f, 6.5f)
                curveTo(3f, 4.567f, 4.567f, 3f, 6.5f, 3f)
                curveTo(8.433f, 3f, 10f, 4.567f, 10f, 6.5f)
                close()
                moveTo(9.30884f, 10.0159f)
                curveTo(8.53901f, 10.6318f, 7.56251f, 11f, 6.5f, 11f)
                curveTo(4.01472f, 11f, 2f, 8.98528f, 2f, 6.5f)
                curveTo(2f, 4.01472f, 4.01472f, 2f, 6.5f, 2f)
                curveTo(8.98528f, 2f, 11f, 4.01472f, 11f, 6.5f)
                curveTo(11f, 7.56251f, 10.6318f, 8.53901f, 10.0159f, 9.30884f)
                lineTo(12.8536f, 12.1464f)
                curveTo(13.0488f, 12.3417f, 13.0488f, 12.6583f, 12.8536f, 12.8536f)
                curveTo(12.6583f, 13.0488f, 12.3417f, 13.0488f, 12.1464f, 12.8536f)
                lineTo(9.30884f, 10.0159f)
                close()
                moveTo(4.25f, 6.5f)
                curveTo(4.25f, 6.22386f, 4.47386f, 6f, 4.75f, 6f)
                horizontalLineTo(6f)
                verticalLineTo(4.75f)
                curveTo(6f, 4.47386f, 6.22386f, 4.25f, 6.5f, 4.25f)
                curveTo(6.77614f, 4.25f, 7f, 4.47386f, 7f, 4.75f)
                verticalLineTo(6f)
                horizontalLineTo(8.25f)
                curveTo(8.52614f, 6f, 8.75f, 6.22386f, 8.75f, 6.5f)
                curveTo(8.75f, 6.77614f, 8.52614f, 7f, 8.25f, 7f)
                horizontalLineTo(7f)
                verticalLineTo(8.25f)
                curveTo(7f, 8.52614f, 6.77614f, 8.75f, 6.5f, 8.75f)
                curveTo(6.22386f, 8.75f, 6f, 8.52614f, 6f, 8.25f)
                verticalLineTo(7f)
                horizontalLineTo(4.75f)
                curveTo(4.47386f, 7f, 4.25f, 6.77614f, 4.25f, 6.5f)
                close()
            }
        }.build()

        return _ZoomIn!!
    }

private var _ZoomIn: ImageVector? = null

