package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Calendar: ImageVector
    get() {
        if (_Calendar != null) {
            return _Calendar!!
        }
        _Calendar = ImageVector.Builder(
            name = "Outlined.Calendar",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(200f, 880f)
                quadTo(167f, 880f, 143.5f, 856.5f)
                quadTo(120f, 833f, 120f, 800f)
                lineTo(120f, 240f)
                quadTo(120f, 207f, 143.5f, 183.5f)
                quadTo(167f, 160f, 200f, 160f)
                lineTo(240f, 160f)
                lineTo(240f, 80f)
                lineTo(320f, 80f)
                lineTo(320f, 160f)
                lineTo(640f, 160f)
                lineTo(640f, 80f)
                lineTo(720f, 80f)
                lineTo(720f, 160f)
                lineTo(760f, 160f)
                quadTo(793f, 160f, 816.5f, 183.5f)
                quadTo(840f, 207f, 840f, 240f)
                lineTo(840f, 800f)
                quadTo(840f, 833f, 816.5f, 856.5f)
                quadTo(793f, 880f, 760f, 880f)
                lineTo(200f, 880f)
                close()
                moveTo(200f, 800f)
                lineTo(760f, 800f)
                quadTo(760f, 800f, 760f, 800f)
                quadTo(760f, 800f, 760f, 800f)
                lineTo(760f, 400f)
                lineTo(200f, 400f)
                lineTo(200f, 800f)
                quadTo(200f, 800f, 200f, 800f)
                quadTo(200f, 800f, 200f, 800f)
                close()
                moveTo(200f, 320f)
                lineTo(760f, 320f)
                lineTo(760f, 240f)
                quadTo(760f, 240f, 760f, 240f)
                quadTo(760f, 240f, 760f, 240f)
                lineTo(200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                lineTo(200f, 320f)
                close()
                moveTo(200f, 320f)
                lineTo(200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                lineTo(200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                quadTo(200f, 240f, 200f, 240f)
                lineTo(200f, 320f)
                close()
            }
        }.build()

        return _Calendar!!
    }

@Suppress("ObjectPropertyName")
private var _Calendar: ImageVector? = null
