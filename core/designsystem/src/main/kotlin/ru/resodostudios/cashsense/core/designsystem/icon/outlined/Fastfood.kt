package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Fastfood: ImageVector
    get() {
        if (_Fastfood != null) {
            return _Fastfood!!
        }
        _Fastfood = ImageVector.Builder(
            name = "Outlined.Fastfood",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(533f, 520f)
                quadToRelative(-32f, -45f, -84.5f, -62.5f)
                reflectiveQuadTo(340f, 440f)
                quadToRelative(-56f, 0f, -108.5f, 17.5f)
                reflectiveQuadTo(147f, 520f)
                horizontalLineToRelative(386f)
                close()
                moveTo(40f, 600f)
                quadToRelative(0f, -109f, 91f, -174.5f)
                reflectiveQuadTo(340f, 360f)
                quadToRelative(118f, 0f, 209f, 65.5f)
                reflectiveQuadTo(640f, 600f)
                lineTo(40f, 600f)
                close()
                moveTo(40f, 760f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(600f)
                verticalLineToRelative(80f)
                lineTo(40f, 760f)
                close()
                moveTo(720f, 920f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(56f)
                lineToRelative(56f, -560f)
                lineTo(450f, 280f)
                lineToRelative(-10f, -80f)
                horizontalLineToRelative(200f)
                verticalLineToRelative(-160f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(160f)
                horizontalLineToRelative(200f)
                lineTo(854f, 862f)
                quadToRelative(-3f, 25f, -22f, 41.5f)
                reflectiveQuadTo(788f, 920f)
                horizontalLineToRelative(-68f)
                close()
                moveTo(720f, 840f)
                horizontalLineToRelative(56f)
                horizontalLineToRelative(-56f)
                close()
                moveTo(80f, 920f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(40f, 880f)
                verticalLineToRelative(-40f)
                horizontalLineToRelative(600f)
                verticalLineToRelative(40f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(600f, 920f)
                lineTo(80f, 920f)
                close()
                moveTo(340f, 520f)
                close()
            }
        }.build()

        return _Fastfood!!
    }

@Suppress("ObjectPropertyName")
private var _Fastfood: ImageVector? = null
