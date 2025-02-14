package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.SelfCare: ImageVector
    get() {
        if (_SelfCare != null) {
            return _SelfCare!!
        }
        _SelfCare = ImageVector.Builder(
            name = "Outlined.SelfCare",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(640f, 920f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 880f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 840f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-40f)
                lineTo(640f, 800f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 760f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 720f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-40f)
                lineTo(640f, 680f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 640f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 600f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-40f)
                lineTo(640f, 560f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 520f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 480f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-40f)
                lineTo(640f, 440f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 400f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 360f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-40f)
                lineTo(640f, 320f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(600f, 280f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(640f, 240f)
                horizontalLineToRelative(160f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(880f, 320f)
                verticalLineToRelative(520f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(800f, 920f)
                lineTo(640f, 920f)
                close()
                moveTo(320f, 600f)
                quadToRelative(66f, 0f, 113f, -65f)
                reflectiveQuadToRelative(47f, -155f)
                quadToRelative(0f, -90f, -47f, -155f)
                reflectiveQuadToRelative(-113f, -65f)
                quadToRelative(-66f, 0f, -113f, 65f)
                reflectiveQuadToRelative(-47f, 155f)
                quadToRelative(0f, 90f, 47f, 155f)
                reflectiveQuadToRelative(113f, 65f)
                close()
                moveTo(320f, 920f)
                quadToRelative(-48f, 0f, -79f, -35.5f)
                reflectiveQuadTo(217f, 801f)
                lineToRelative(16f, -141f)
                quadToRelative(-68f, -33f, -110.5f, -108.5f)
                reflectiveQuadTo(80f, 380f)
                quadToRelative(0f, -125f, 70f, -212.5f)
                reflectiveQuadTo(320f, 80f)
                quadToRelative(100f, 0f, 170f, 87.5f)
                reflectiveQuadTo(560f, 380f)
                quadToRelative(0f, 96f, -42.5f, 171.5f)
                reflectiveQuadTo(407f, 660f)
                lineToRelative(16f, 141f)
                quadToRelative(7f, 48f, -24f, 83.5f)
                reflectiveQuadTo(320f, 920f)
                close()
            }
        }.build()

        return _SelfCare!!
    }

@Suppress("ObjectPropertyName")
private var _SelfCare: ImageVector? = null
