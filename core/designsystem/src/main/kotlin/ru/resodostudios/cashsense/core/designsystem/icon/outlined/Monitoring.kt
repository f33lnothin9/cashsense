package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Monitoring: ImageVector
    get() {
        if (_Monitoring != null) {
            return _Monitoring!!
        }
        _Monitoring = ImageVector.Builder(
            name = "Outlined.Monitoring",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(120f, 840f)
                verticalLineToRelative(-80f)
                lineToRelative(80f, -80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(280f, 840f)
                verticalLineToRelative(-240f)
                lineToRelative(80f, -80f)
                verticalLineToRelative(320f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(440f, 840f)
                verticalLineToRelative(-320f)
                lineToRelative(80f, 81f)
                verticalLineToRelative(239f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(600f, 840f)
                verticalLineToRelative(-239f)
                lineToRelative(80f, -80f)
                verticalLineToRelative(319f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(760f, 840f)
                verticalLineToRelative(-400f)
                lineToRelative(80f, -80f)
                verticalLineToRelative(480f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(120f, 633f)
                verticalLineToRelative(-113f)
                lineToRelative(280f, -280f)
                lineToRelative(160f, 160f)
                lineToRelative(280f, -280f)
                verticalLineToRelative(113f)
                lineTo(560f, 513f)
                lineTo(400f, 353f)
                lineTo(120f, 633f)
                close()
            }
        }.build()

        return _Monitoring!!
    }

@Suppress("ObjectPropertyName")
private var _Monitoring: ImageVector? = null
