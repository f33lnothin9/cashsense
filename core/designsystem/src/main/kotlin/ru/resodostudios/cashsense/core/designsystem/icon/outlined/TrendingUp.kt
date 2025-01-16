package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.TrendingUp: ImageVector
    get() {
        if (_TrendingUp != null) {
            return _TrendingUp!!
        }
        _TrendingUp = ImageVector.Builder(
            name = "Outlined.TrendingUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveToRelative(136f, 720f)
                lineToRelative(-56f, -56f)
                lineToRelative(296f, -298f)
                lineToRelative(160f, 160f)
                lineToRelative(208f, -206f)
                lineTo(640f, 320f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-104f)
                lineTo(536f, 640f)
                lineTo(376f, 480f)
                lineTo(136f, 720f)
                close()
            }
        }.build()

        return _TrendingUp!!
    }

@Suppress("ObjectPropertyName")
private var _TrendingUp: ImageVector? = null
