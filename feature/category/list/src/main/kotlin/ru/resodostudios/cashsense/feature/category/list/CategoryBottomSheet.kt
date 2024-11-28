package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.designsystem.component.CsModalBottomSheet
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.StoredIcon
import ru.resodostudios.cashsense.core.locales.R as localesR

@Composable
internal fun CategoryBottomSheet(
    category: Category,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: (String) -> Unit,
) {
    CsModalBottomSheet(onDismiss) {
        Column {
            CsListItem(
                headlineContent = { Text(category.title.toString()) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(StoredIcon.asRes(category.iconId)),
                        contentDescription = null,
                    )
                },
            )
            HorizontalDivider(Modifier.padding(16.dp))
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.edit)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Edit),
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onEdit()
                },
            )
            CsListItem(
                headlineContent = { Text(stringResource(localesR.string.delete)) },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CsIcons.Delete),
                        contentDescription = null,
                    )
                },
                onClick = {
                    onDismiss()
                    onDelete(category.id.toString())
                },
            )
        }
    }
}