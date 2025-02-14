package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.SendMoney: ImageVector
    get() {
        if (_SendMoney != null) {
            return _SendMoney!!
        }
        _SendMoney = ImageVector.Builder(
            name = "Outlined.SendMoney",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(240f, 790f)
                quadTo(136f, 763f, 68f, 678f)
                quadTo(0f, 593f, 0f, 480f)
                quadTo(0f, 367f, 68f, 282f)
                quadTo(136f, 197f, 240f, 170f)
                lineTo(240f, 254f)
                quadTo(169f, 278f, 124.5f, 340f)
                quadTo(80f, 402f, 80f, 480f)
                quadTo(80f, 558f, 124.5f, 620f)
                quadTo(169f, 682f, 240f, 706f)
                lineTo(240f, 790f)
                close()
                moveTo(560f, 800f)
                quadTo(427f, 800f, 333.5f, 706.5f)
                quadTo(240f, 613f, 240f, 480f)
                quadTo(240f, 347f, 333.5f, 253.5f)
                quadTo(427f, 160f, 560f, 160f)
                quadTo(626f, 160f, 684f, 185f)
                quadTo(742f, 210f, 786f, 254f)
                lineTo(730f, 310f)
                quadTo(697f, 277f, 653.5f, 258.5f)
                quadTo(610f, 240f, 560f, 240f)
                quadTo(460f, 240f, 390f, 310f)
                quadTo(320f, 380f, 320f, 480f)
                quadTo(320f, 580f, 390f, 650f)
                quadTo(460f, 720f, 560f, 720f)
                quadTo(610f, 720f, 653.5f, 701.5f)
                quadTo(697f, 683f, 730f, 650f)
                lineTo(786f, 706f)
                quadTo(742f, 750f, 684f, 775f)
                quadTo(626f, 800f, 560f, 800f)
                close()
                moveTo(800f, 640f)
                lineTo(744f, 584f)
                lineTo(808f, 520f)
                lineTo(520f, 520f)
                lineTo(520f, 440f)
                lineTo(808f, 440f)
                lineTo(744f, 376f)
                lineTo(800f, 320f)
                lineTo(960f, 480f)
                lineTo(800f, 640f)
                close()
            }
        }.build()

        return _SendMoney!!
    }

@Suppress("ObjectPropertyName")
private var _SendMoney: ImageVector? = null
