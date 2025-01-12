package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Movie: ImageVector
    get() {
        if (_Movie != null) {
            return _Movie!!
        }
        _Movie = ImageVector.Builder(
            name = "Outlined.Movie",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(160f, 160f)
                lineTo(240f, 320f)
                lineTo(360f, 320f)
                lineTo(280f, 160f)
                lineTo(360f, 160f)
                lineTo(440f, 320f)
                lineTo(560f, 320f)
                lineTo(480f, 160f)
                lineTo(560f, 160f)
                lineTo(640f, 320f)
                lineTo(760f, 320f)
                lineTo(680f, 160f)
                lineTo(800f, 160f)
                quadTo(833f, 160f, 856.5f, 183.5f)
                quadTo(880f, 207f, 880f, 240f)
                lineTo(880f, 720f)
                quadTo(880f, 753f, 856.5f, 776.5f)
                quadTo(833f, 800f, 800f, 800f)
                lineTo(160f, 800f)
                quadTo(127f, 800f, 103.5f, 776.5f)
                quadTo(80f, 753f, 80f, 720f)
                lineTo(80f, 240f)
                quadTo(80f, 207f, 103.5f, 183.5f)
                quadTo(127f, 160f, 160f, 160f)
                close()
                moveTo(160f, 400f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(800f, 720f)
                quadTo(800f, 720f, 800f, 720f)
                quadTo(800f, 720f, 800f, 720f)
                lineTo(800f, 400f)
                lineTo(160f, 400f)
                close()
                moveTo(160f, 400f)
                lineTo(160f, 400f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(160f, 400f)
                close()
            }
        }.build()

        return _Movie!!
    }

@Suppress("ObjectPropertyName")
private var _Movie: ImageVector? = null
