package ru.resodostudios.cashsense.core.designsystem.icon.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Filled.Settings: ImageVector
    get() {
        if (_Settings != null) {
            return _Settings!!
        }
        _Settings = ImageVector.Builder(
            name = "Filled.Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(370f, 880f)
                lineTo(354f, 752f)
                quadTo(341f, 747f, 329.5f, 740f)
                quadTo(318f, 733f, 307f, 725f)
                lineTo(188f, 775f)
                lineTo(78f, 585f)
                lineTo(181f, 507f)
                quadTo(180f, 500f, 180f, 493.5f)
                quadTo(180f, 487f, 180f, 480f)
                quadTo(180f, 473f, 180f, 466.5f)
                quadTo(180f, 460f, 181f, 453f)
                lineTo(78f, 375f)
                lineTo(188f, 185f)
                lineTo(307f, 235f)
                quadTo(318f, 227f, 330f, 220f)
                quadTo(342f, 213f, 354f, 208f)
                lineTo(370f, 80f)
                lineTo(590f, 80f)
                lineTo(606f, 208f)
                quadTo(619f, 213f, 630.5f, 220f)
                quadTo(642f, 227f, 653f, 235f)
                lineTo(772f, 185f)
                lineTo(882f, 375f)
                lineTo(779f, 453f)
                quadTo(780f, 460f, 780f, 466.5f)
                quadTo(780f, 473f, 780f, 480f)
                quadTo(780f, 487f, 780f, 493.5f)
                quadTo(780f, 500f, 778f, 507f)
                lineTo(881f, 585f)
                lineTo(771f, 775f)
                lineTo(653f, 725f)
                quadTo(642f, 733f, 630f, 740f)
                quadTo(618f, 747f, 606f, 752f)
                lineTo(590f, 880f)
                lineTo(370f, 880f)
                close()
                moveTo(482f, 620f)
                quadTo(540f, 620f, 581f, 579f)
                quadTo(622f, 538f, 622f, 480f)
                quadTo(622f, 422f, 581f, 381f)
                quadTo(540f, 340f, 482f, 340f)
                quadTo(423f, 340f, 382.5f, 381f)
                quadTo(342f, 422f, 342f, 480f)
                quadTo(342f, 538f, 382.5f, 579f)
                quadTo(423f, 620f, 482f, 620f)
                close()
            }
        }.build()

        return _Settings!!
    }

@Suppress("ObjectPropertyName")
private var _Settings: ImageVector? = null
