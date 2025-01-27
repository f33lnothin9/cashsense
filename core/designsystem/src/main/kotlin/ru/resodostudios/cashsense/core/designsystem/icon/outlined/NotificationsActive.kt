package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.NotificationsActive: ImageVector
    get() {
        if (_NotificationsActive != null) {
            return _NotificationsActive!!
        }
        _NotificationsActive = ImageVector.Builder(
            name = "Outlined.NotificationsActive",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(80f, 400f)
                quadTo(80f, 300f, 124.5f, 216.5f)
                quadTo(169f, 133f, 244f, 78f)
                lineTo(291f, 142f)
                quadTo(231f, 186f, 195.5f, 253f)
                quadTo(160f, 320f, 160f, 400f)
                lineTo(80f, 400f)
                close()
                moveTo(800f, 400f)
                quadTo(800f, 320f, 764.5f, 253f)
                quadTo(729f, 186f, 669f, 142f)
                lineTo(716f, 78f)
                quadTo(791f, 133f, 835.5f, 216.5f)
                quadTo(880f, 300f, 880f, 400f)
                lineTo(800f, 400f)
                close()
                moveTo(160f, 760f)
                lineTo(160f, 680f)
                lineTo(240f, 680f)
                lineTo(240f, 400f)
                quadTo(240f, 317f, 290f, 252.5f)
                quadTo(340f, 188f, 420f, 168f)
                lineTo(420f, 140f)
                quadTo(420f, 115f, 437.5f, 97.5f)
                quadTo(455f, 80f, 480f, 80f)
                quadTo(505f, 80f, 522.5f, 97.5f)
                quadTo(540f, 115f, 540f, 140f)
                lineTo(540f, 168f)
                quadTo(620f, 188f, 670f, 252.5f)
                quadTo(720f, 317f, 720f, 400f)
                lineTo(720f, 680f)
                lineTo(800f, 680f)
                lineTo(800f, 760f)
                lineTo(160f, 760f)
                close()
                moveTo(480f, 460f)
                lineTo(480f, 460f)
                lineTo(480f, 460f)
                lineTo(480f, 460f)
                quadTo(480f, 460f, 480f, 460f)
                quadTo(480f, 460f, 480f, 460f)
                quadTo(480f, 460f, 480f, 460f)
                quadTo(480f, 460f, 480f, 460f)
                close()
                moveTo(480f, 880f)
                quadTo(447f, 880f, 423.5f, 856.5f)
                quadTo(400f, 833f, 400f, 800f)
                lineTo(560f, 800f)
                quadTo(560f, 833f, 536.5f, 856.5f)
                quadTo(513f, 880f, 480f, 880f)
                close()
                moveTo(320f, 680f)
                lineTo(640f, 680f)
                lineTo(640f, 400f)
                quadTo(640f, 334f, 593f, 287f)
                quadTo(546f, 240f, 480f, 240f)
                quadTo(414f, 240f, 367f, 287f)
                quadTo(320f, 334f, 320f, 400f)
                lineTo(320f, 680f)
                close()
            }
        }.build()

        return _NotificationsActive!!
    }

@Suppress("ObjectPropertyName")
private var _NotificationsActive: ImageVector? = null
