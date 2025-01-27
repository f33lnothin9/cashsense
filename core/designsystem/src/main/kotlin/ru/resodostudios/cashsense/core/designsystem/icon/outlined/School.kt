package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.School: ImageVector
    get() {
        if (_School != null) {
            return _School!!
        }
        _School = ImageVector.Builder(
            name = "Outlined.School",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 840f)
                lineTo(200f, 688f)
                verticalLineToRelative(-240f)
                lineTo(40f, 360f)
                lineToRelative(440f, -240f)
                lineToRelative(440f, 240f)
                verticalLineToRelative(320f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-276f)
                lineToRelative(-80f, 44f)
                verticalLineToRelative(240f)
                lineTo(480f, 840f)
                close()
                moveTo(480f, 508f)
                lineTo(754f, 360f)
                lineTo(480f, 212f)
                lineTo(206f, 360f)
                lineTo(480f, 508f)
                close()
                moveTo(480f, 749f)
                lineTo(680f, 641f)
                verticalLineToRelative(-151f)
                lineTo(480f, 600f)
                lineTo(280f, 490f)
                verticalLineToRelative(151f)
                lineToRelative(200f, 108f)
                close()
                moveTo(480f, 508f)
                close()
                moveTo(480f, 598f)
                close()
                moveTo(480f, 598f)
                close()
            }
        }.build()

        return _School!!
    }

@Suppress("ObjectPropertyName")
private var _School: ImageVector? = null
