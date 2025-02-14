package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Phishing: ImageVector
    get() {
        if (_Phishing != null) {
            return _Phishing!!
        }
        _Phishing = ImageVector.Builder(
            name = "Outlined.Phishing",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(440f, 840f)
                quadToRelative(-100f, 0f, -170f, -70f)
                reflectiveQuadToRelative(-70f, -170f)
                verticalLineToRelative(-240f)
                lineToRelative(200f, 200f)
                lineToRelative(-56f, 57f)
                lineToRelative(-64f, -64f)
                verticalLineToRelative(47f)
                quadToRelative(0f, 66f, 47f, 113f)
                reflectiveQuadToRelative(113f, 47f)
                quadToRelative(66f, 0f, 113f, -47f)
                reflectiveQuadToRelative(47f, -113f)
                verticalLineToRelative(-127f)
                quadToRelative(-36f, -14f, -58f, -44.5f)
                reflectiveQuadTo(520f, 360f)
                quadToRelative(0f, -38f, 22f, -68.5f)
                reflectiveQuadToRelative(58f, -44.5f)
                verticalLineToRelative(-167f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(167f)
                quadToRelative(36f, 14f, 58f, 44.5f)
                reflectiveQuadToRelative(22f, 68.5f)
                quadToRelative(0f, 38f, -22f, 69f)
                reflectiveQuadToRelative(-58f, 44f)
                verticalLineToRelative(127f)
                quadToRelative(0f, 100f, -70f, 170f)
                reflectiveQuadToRelative(-170f, 70f)
                close()
                moveTo(640f, 400f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(680f, 360f)
                quadToRelative(0f, -17f, -11.5f, -28.5f)
                reflectiveQuadTo(640f, 320f)
                quadToRelative(-17f, 0f, -28.5f, 11.5f)
                reflectiveQuadTo(600f, 360f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(640f, 400f)
                close()
                moveTo(640f, 360f)
                close()
            }
        }.build()

        return _Phishing!!
    }

@Suppress("ObjectPropertyName")
private var _Phishing: ImageVector? = null
