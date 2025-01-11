package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Block: ImageVector
    get() {
        if (_Block != null) {
            return _Block!!
        }
        _Block = ImageVector.Builder(
            name = "Outlined.Block",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
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
                quadTo(534f, 800f, 584f, 782.5f)
                quadTo(634f, 765f, 676f, 732f)
                lineTo(228f, 284f)
                quadTo(195f, 326f, 177.5f, 376f)
                quadTo(160f, 426f, 160f, 480f)
                quadTo(160f, 614f, 253f, 707f)
                quadTo(346f, 800f, 480f, 800f)
                close()
                moveTo(732f, 676f)
                quadTo(765f, 634f, 782.5f, 584f)
                quadTo(800f, 534f, 800f, 480f)
                quadTo(800f, 346f, 707f, 253f)
                quadTo(614f, 160f, 480f, 160f)
                quadTo(426f, 160f, 376f, 177.5f)
                quadTo(326f, 195f, 284f, 228f)
                lineTo(732f, 676f)
                close()
            }
        }.build()

        return _Block!!
    }

@Suppress("ObjectPropertyName")
private var _Block: ImageVector? = null
