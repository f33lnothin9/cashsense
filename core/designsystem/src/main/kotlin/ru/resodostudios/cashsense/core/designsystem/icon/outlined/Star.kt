package ru.resodostudios.cashsense.core.designsystem.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons

val CsIcons.Outlined.Star: ImageVector
    get() {
        if (_Star != null) {
            return _Star!!
        }
        _Star = ImageVector.Builder(
            name = "Outlined.Star",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(354f, 673f)
                lineTo(480f, 597f)
                lineTo(606f, 674f)
                lineTo(573f, 530f)
                lineTo(684f, 434f)
                lineTo(538f, 421f)
                lineTo(480f, 285f)
                lineTo(422f, 420f)
                lineTo(276f, 433f)
                lineTo(387f, 530f)
                lineTo(354f, 673f)
                close()
                moveTo(233f, 840f)
                lineTo(298f, 559f)
                lineTo(80f, 370f)
                lineTo(368f, 345f)
                lineTo(480f, 80f)
                lineTo(592f, 345f)
                lineTo(880f, 370f)
                lineTo(662f, 559f)
                lineTo(727f, 840f)
                lineTo(480f, 691f)
                lineTo(233f, 840f)
                close()
                moveTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                lineTo(480f, 490f)
                close()
            }
        }.build()

        return _Star!!
    }

@Suppress("ObjectPropertyName")
private var _Star: ImageVector? = null
