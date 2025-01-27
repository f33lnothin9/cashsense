package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.AccountBalance: ImageVector
    get() {
        if (_AccountBalance != null) {
            return _AccountBalance!!
        }
        _AccountBalance = ImageVector.Builder(
            name = "Outlined.AccountBalance",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(200f, 680f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(280f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(440f, 680f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(280f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(80f, 840f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(800f)
                verticalLineToRelative(80f)
                lineTo(80f, 840f)
                close()
                moveTo(680f, 680f)
                verticalLineToRelative(-280f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(280f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(80f, 320f)
                verticalLineToRelative(-80f)
                lineToRelative(400f, -200f)
                lineToRelative(400f, 200f)
                verticalLineToRelative(80f)
                lineTo(80f, 320f)
                close()
                moveTo(258f, 240f)
                horizontalLineToRelative(444f)
                horizontalLineToRelative(-444f)
                close()
                moveTo(258f, 240f)
                horizontalLineToRelative(444f)
                lineTo(480f, 130f)
                lineTo(258f, 240f)
                close()
            }
        }.build()

        return _AccountBalance!!
    }

@Suppress("ObjectPropertyName")
private var _AccountBalance: ImageVector? = null
