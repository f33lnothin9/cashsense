package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Book: ImageVector
    get() {
        if (_Book != null) {
            return _Book!!
        }
        _Book = ImageVector.Builder(
            name = "Outlined.Book",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(300f, 880f)
                quadTo(242f, 880f, 201f, 839f)
                quadTo(160f, 798f, 160f, 740f)
                lineTo(160f, 220f)
                quadTo(160f, 162f, 201f, 121f)
                quadTo(242f, 80f, 300f, 80f)
                lineTo(800f, 80f)
                lineTo(800f, 680f)
                quadTo(775f, 680f, 757.5f, 697.5f)
                quadTo(740f, 715f, 740f, 740f)
                quadTo(740f, 765f, 757.5f, 782.5f)
                quadTo(775f, 800f, 800f, 800f)
                lineTo(800f, 880f)
                lineTo(300f, 880f)
                close()
                moveTo(240f, 613f)
                quadTo(254f, 606f, 269f, 603f)
                quadTo(284f, 600f, 300f, 600f)
                lineTo(320f, 600f)
                lineTo(320f, 160f)
                lineTo(300f, 160f)
                quadTo(275f, 160f, 257.5f, 177.5f)
                quadTo(240f, 195f, 240f, 220f)
                lineTo(240f, 613f)
                close()
                moveTo(400f, 600f)
                lineTo(720f, 600f)
                lineTo(720f, 160f)
                lineTo(400f, 160f)
                lineTo(400f, 600f)
                close()
                moveTo(240f, 613f)
                quadTo(240f, 613f, 240f, 613f)
                quadTo(240f, 613f, 240f, 613f)
                lineTo(240f, 613f)
                lineTo(240f, 160f)
                lineTo(240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                quadTo(240f, 160f, 240f, 160f)
                lineTo(240f, 613f)
                close()
                moveTo(300f, 800f)
                lineTo(673f, 800f)
                quadTo(667f, 786f, 663.5f, 771.5f)
                quadTo(660f, 757f, 660f, 740f)
                quadTo(660f, 724f, 663f, 709f)
                quadTo(666f, 694f, 673f, 680f)
                lineTo(300f, 680f)
                quadTo(274f, 680f, 257f, 697.5f)
                quadTo(240f, 715f, 240f, 740f)
                quadTo(240f, 766f, 257f, 783f)
                quadTo(274f, 800f, 300f, 800f)
                close()
            }
        }.build()

        return _Book!!
    }

@Suppress("ObjectPropertyName")
private var _Book: ImageVector? = null
