package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.LocalBar: ImageVector
    get() {
        if (_LocalBar != null) {
            return _LocalBar!!
        }
        _LocalBar = ImageVector.Builder(
            name = "Outlined.LocalBar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 840f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-200f)
                lineTo(120f, 200f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(720f)
                verticalLineToRelative(80f)
                lineTo(520f, 560f)
                verticalLineToRelative(200f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(80f)
                lineTo(240f, 840f)
                close()
                moveTo(298f, 280f)
                horizontalLineToRelative(364f)
                lineToRelative(72f, -80f)
                lineTo(226f, 200f)
                lineToRelative(72f, 80f)
                close()
                moveTo(480f, 484f)
                lineTo(591f, 360f)
                lineTo(369f, 360f)
                lineToRelative(111f, 124f)
                close()
                moveTo(480f, 484f)
                close()
            }
        }.build()

        return _LocalBar!!
    }

@Suppress("ObjectPropertyName")
private var _LocalBar: ImageVector? = null
