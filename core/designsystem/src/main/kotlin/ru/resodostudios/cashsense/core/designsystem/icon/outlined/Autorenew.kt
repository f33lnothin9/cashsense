package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Autorenew: ImageVector
    get() {
        if (_Autorenew != null) {
            return _Autorenew!!
        }
        _Autorenew = ImageVector.Builder(
            name = "Outlined.Autorenew",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(204f, 642f)
                quadTo(182f, 604f, 171f, 564f)
                quadTo(160f, 524f, 160f, 482f)
                quadTo(160f, 348f, 253f, 254f)
                quadTo(346f, 160f, 480f, 160f)
                lineTo(487f, 160f)
                lineTo(423f, 96f)
                lineTo(479f, 40f)
                lineTo(639f, 200f)
                lineTo(479f, 360f)
                lineTo(423f, 304f)
                lineTo(487f, 240f)
                lineTo(480f, 240f)
                quadTo(380f, 240f, 310f, 310.5f)
                quadTo(240f, 381f, 240f, 482f)
                quadTo(240f, 508f, 246f, 533f)
                quadTo(252f, 558f, 264f, 582f)
                lineTo(204f, 642f)
                close()
                moveTo(481f, 920f)
                lineTo(321f, 760f)
                lineTo(481f, 600f)
                lineTo(537f, 656f)
                lineTo(473f, 720f)
                lineTo(480f, 720f)
                quadTo(580f, 720f, 650f, 649.5f)
                quadTo(720f, 579f, 720f, 478f)
                quadTo(720f, 452f, 714f, 427f)
                quadTo(708f, 402f, 696f, 378f)
                lineTo(756f, 318f)
                quadTo(778f, 356f, 789f, 396f)
                quadTo(800f, 436f, 800f, 478f)
                quadTo(800f, 612f, 707f, 706f)
                quadTo(614f, 800f, 480f, 800f)
                lineTo(473f, 800f)
                lineTo(537f, 864f)
                lineTo(481f, 920f)
                close()
            }
        }.build()

        return _Autorenew!!
    }

@Suppress("ObjectPropertyName")
private var _Autorenew: ImageVector? = null
