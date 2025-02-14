package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Pending: ImageVector
    get() {
        if (_Pending != null) {
            return _Pending!!
        }
        _Pending = ImageVector.Builder(
            name = "Outlined.Pending",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(280f, 540f)
                quadTo(305f, 540f, 322.5f, 522.5f)
                quadTo(340f, 505f, 340f, 480f)
                quadTo(340f, 455f, 322.5f, 437.5f)
                quadTo(305f, 420f, 280f, 420f)
                quadTo(255f, 420f, 237.5f, 437.5f)
                quadTo(220f, 455f, 220f, 480f)
                quadTo(220f, 505f, 237.5f, 522.5f)
                quadTo(255f, 540f, 280f, 540f)
                close()
                moveTo(480f, 540f)
                quadTo(505f, 540f, 522.5f, 522.5f)
                quadTo(540f, 505f, 540f, 480f)
                quadTo(540f, 455f, 522.5f, 437.5f)
                quadTo(505f, 420f, 480f, 420f)
                quadTo(455f, 420f, 437.5f, 437.5f)
                quadTo(420f, 455f, 420f, 480f)
                quadTo(420f, 505f, 437.5f, 522.5f)
                quadTo(455f, 540f, 480f, 540f)
                close()
                moveTo(680f, 540f)
                quadTo(705f, 540f, 722.5f, 522.5f)
                quadTo(740f, 505f, 740f, 480f)
                quadTo(740f, 455f, 722.5f, 437.5f)
                quadTo(705f, 420f, 680f, 420f)
                quadTo(655f, 420f, 637.5f, 437.5f)
                quadTo(620f, 455f, 620f, 480f)
                quadTo(620f, 505f, 637.5f, 522.5f)
                quadTo(655f, 540f, 680f, 540f)
                close()
                moveTo(480f, 880f)
                quadTo(397f, 880f, 324f, 848.5f)
                quadTo(251f, 817f, 197f, 763f)
                quadTo(143f, 709f, 111.5f, 636f)
                quadTo(80f, 563f, 80f, 480f)
                quadTo(80f, 397f, 111.5f, 324f)
                quadTo(143f, 251f, 197f, 197f)
                quadTo(251f, 143f, 324f, 111.5f)
                quadTo(397f, 80f, 480f, 80f)
                quadTo(563f, 80f, 636f, 111.5f)
                quadTo(709f, 143f, 763f, 197f)
                quadTo(817f, 251f, 848.5f, 324f)
                quadTo(880f, 397f, 880f, 480f)
                quadTo(880f, 563f, 848.5f, 636f)
                quadTo(817f, 709f, 763f, 763f)
                quadTo(709f, 817f, 636f, 848.5f)
                quadTo(563f, 880f, 480f, 880f)
                close()
                moveTo(480f, 800f)
                quadTo(614f, 800f, 707f, 707f)
                quadTo(800f, 614f, 800f, 480f)
                quadTo(800f, 346f, 707f, 253f)
                quadTo(614f, 160f, 480f, 160f)
                quadTo(346f, 160f, 253f, 253f)
                quadTo(160f, 346f, 160f, 480f)
                quadTo(160f, 614f, 253f, 707f)
                quadTo(346f, 800f, 480f, 800f)
                close()
                moveTo(480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                quadTo(480f, 480f, 480f, 480f)
                close()
            }
        }.build()

        return _Pending!!
    }

@Suppress("ObjectPropertyName")
private var _Pending: ImageVector? = null
