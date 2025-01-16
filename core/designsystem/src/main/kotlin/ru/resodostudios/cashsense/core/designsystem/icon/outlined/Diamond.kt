package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Diamond: ImageVector
    get() {
        if (_Diamond != null) {
            return _Diamond!!
        }
        _Diamond = ImageVector.Builder(
            name = "Outlined.Diamond",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(480f, 840f)
                lineTo(80f, 360f)
                lineTo(200f, 120f)
                lineTo(760f, 120f)
                lineTo(880f, 360f)
                lineTo(480f, 840f)
                close()
                moveTo(385f, 320f)
                lineTo(575f, 320f)
                lineTo(515f, 200f)
                lineTo(445f, 200f)
                lineTo(385f, 320f)
                close()
                moveTo(440f, 667f)
                lineTo(440f, 400f)
                lineTo(218f, 400f)
                lineTo(440f, 667f)
                close()
                moveTo(520f, 667f)
                lineTo(742f, 400f)
                lineTo(520f, 400f)
                lineTo(520f, 667f)
                close()
                moveTo(664f, 320f)
                lineTo(770f, 320f)
                lineTo(710f, 200f)
                lineTo(604f, 200f)
                lineTo(664f, 320f)
                close()
                moveTo(190f, 320f)
                lineTo(296f, 320f)
                lineTo(356f, 200f)
                lineTo(250f, 200f)
                lineTo(190f, 320f)
                close()
            }
        }.build()

        return _Diamond!!
    }

@Suppress("ObjectPropertyName")
private var _Diamond: ImageVector? = null
