package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.ReceiptLong: ImageVector
    get() {
        if (_ReceiptLong != null) {
            return _ReceiptLong!!
        }
        _ReceiptLong = ImageVector.Builder(
            name = "Outlined.ReceiptLong",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 880f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(120f)
                verticalLineToRelative(-560f)
                lineToRelative(60f, 60f)
                lineToRelative(60f, -60f)
                lineToRelative(60f, 60f)
                lineToRelative(60f, -60f)
                lineToRelative(60f, 60f)
                lineToRelative(60f, -60f)
                lineToRelative(60f, 60f)
                lineToRelative(60f, -60f)
                lineToRelative(60f, 60f)
                lineToRelative(60f, -60f)
                verticalLineToRelative(680f)
                quadToRelative(0f, 50f, -35f, 85f)
                reflectiveQuadToRelative(-85f, 35f)
                lineTo(240f, 880f)
                close()
                moveTo(720f, 800f)
                quadToRelative(17f, 0f, 28.5f, -11.5f)
                reflectiveQuadTo(760f, 760f)
                verticalLineToRelative(-560f)
                lineTo(320f, 200f)
                verticalLineToRelative(440f)
                horizontalLineToRelative(360f)
                verticalLineToRelative(120f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(720f, 800f)
                close()
                moveTo(360f, 360f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                lineTo(360f, 360f)
                close()
                moveTo(360f, 480f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                lineTo(360f, 480f)
                close()
                moveTo(680f, 360f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(640f, 320f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(680f, 280f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(720f, 320f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(680f, 360f)
                close()
                moveTo(680f, 480f)
                quadToRelative(-17f, 0f, -28.5f, -11.5f)
                reflectiveQuadTo(640f, 440f)
                quadToRelative(0f, -17f, 11.5f, -28.5f)
                reflectiveQuadTo(680f, 400f)
                quadToRelative(17f, 0f, 28.5f, 11.5f)
                reflectiveQuadTo(720f, 440f)
                quadToRelative(0f, 17f, -11.5f, 28.5f)
                reflectiveQuadTo(680f, 480f)
                close()
                moveTo(240f, 800f)
                horizontalLineToRelative(360f)
                verticalLineToRelative(-80f)
                lineTo(200f, 720f)
                verticalLineToRelative(40f)
                quadToRelative(0f, 17f, 11.5f, 28.5f)
                reflectiveQuadTo(240f, 800f)
                close()
                moveTo(200f, 800f)
                verticalLineToRelative(-80f)
                verticalLineToRelative(80f)
                close()
            }
        }.build()

        return _ReceiptLong!!
    }

@Suppress("ObjectPropertyName")
private var _ReceiptLong: ImageVector? = null
