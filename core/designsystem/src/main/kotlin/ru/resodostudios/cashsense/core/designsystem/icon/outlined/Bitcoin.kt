package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Bitcoin: ImageVector
    get() {
        if (_Bitcoin != null) {
            return _Bitcoin!!
        }
        _Bitcoin = ImageVector.Builder(
            name = "Outlined.Bitcoin",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(360f, 840f)
                lineTo(360f, 760f)
                lineTo(240f, 760f)
                lineTo(240f, 680f)
                lineTo(320f, 680f)
                lineTo(320f, 280f)
                lineTo(240f, 280f)
                lineTo(240f, 200f)
                lineTo(360f, 200f)
                lineTo(360f, 120f)
                lineTo(440f, 120f)
                lineTo(440f, 200f)
                lineTo(520f, 200f)
                lineTo(520f, 120f)
                lineTo(600f, 120f)
                lineTo(600f, 205f)
                lineTo(600f, 205f)
                quadTo(652f, 219f, 686f, 261.5f)
                quadTo(720f, 304f, 720f, 360f)
                quadTo(720f, 389f, 710f, 415.5f)
                quadTo(700f, 442f, 682f, 463f)
                quadTo(717f, 484f, 738.5f, 520f)
                quadTo(760f, 556f, 760f, 600f)
                quadTo(760f, 666f, 713f, 713f)
                quadTo(666f, 760f, 600f, 760f)
                lineTo(600f, 760f)
                lineTo(600f, 840f)
                lineTo(520f, 840f)
                lineTo(520f, 760f)
                lineTo(440f, 760f)
                lineTo(440f, 840f)
                lineTo(360f, 840f)
                close()
                moveTo(400f, 440f)
                lineTo(560f, 440f)
                quadTo(593f, 440f, 616.5f, 416.5f)
                quadTo(640f, 393f, 640f, 360f)
                quadTo(640f, 327f, 616.5f, 303.5f)
                quadTo(593f, 280f, 560f, 280f)
                lineTo(400f, 280f)
                lineTo(400f, 440f)
                close()
                moveTo(400f, 680f)
                lineTo(600f, 680f)
                quadTo(633f, 680f, 656.5f, 656.5f)
                quadTo(680f, 633f, 680f, 600f)
                quadTo(680f, 567f, 656.5f, 543.5f)
                quadTo(633f, 520f, 600f, 520f)
                lineTo(400f, 520f)
                lineTo(400f, 680f)
                close()
            }
        }.build()

        return _Bitcoin!!
    }

@Suppress("ObjectPropertyName")
private var _Bitcoin: ImageVector? = null
