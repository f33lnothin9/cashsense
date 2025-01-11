package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Home: ImageVector
    get() {
        if (_Home != null) {
            return _Home!!
        }
        _Home = ImageVector.Builder(
            name = "Filled.Home",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(4f, 21f)
                verticalLineTo(9f)
                lineToRelative(8f, -6f)
                lineToRelative(8f, 6f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(-6f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(7f)
                close()
            }
        }.build()

        return _Home!!
    }

@Suppress("ObjectPropertyName")
private var _Home: ImageVector? = null
