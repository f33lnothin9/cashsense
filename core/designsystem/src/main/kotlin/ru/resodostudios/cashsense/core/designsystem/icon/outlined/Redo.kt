package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Redo: ImageVector
    get() {
        if (_Redo != null) {
            return _Redo!!
        }
        _Redo = ImageVector.Builder(
            name = "Outlined.Redo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(396f, 760f)
                quadTo(299f, 760f, 229.5f, 697f)
                quadTo(160f, 634f, 160f, 540f)
                quadTo(160f, 446f, 229.5f, 383f)
                quadTo(299f, 320f, 396f, 320f)
                lineTo(648f, 320f)
                lineTo(544f, 216f)
                lineTo(600f, 160f)
                lineTo(800f, 360f)
                lineTo(600f, 560f)
                lineTo(544f, 504f)
                lineTo(648f, 400f)
                lineTo(396f, 400f)
                quadTo(333f, 400f, 286.5f, 440f)
                quadTo(240f, 480f, 240f, 540f)
                quadTo(240f, 600f, 286.5f, 640f)
                quadTo(333f, 680f, 396f, 680f)
                lineTo(680f, 680f)
                lineTo(680f, 760f)
                lineTo(396f, 760f)
                close()
            }
        }.build()

        return _Redo!!
    }

@Suppress("ObjectPropertyName")
private var _Redo: ImageVector? = null
