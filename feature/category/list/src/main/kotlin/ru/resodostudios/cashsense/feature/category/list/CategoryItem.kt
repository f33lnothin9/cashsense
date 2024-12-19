package ru.resodostudios.cashsense.feature.category.list

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import ru.resodostudios.cashsense.core.designsystem.component.CsListItem
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.StoredIcon

@Composable
internal fun CategoryItem(
    category: Category,
    modifier: Modifier = Modifier,
    onClick: ((String) -> Unit)? = null,
) {
    CsListItem(
        headlineContent = { Text(category.title.toString()) },
        leadingContent = {
            Icon(
                imageVector = ImageVector.vectorResource(StoredIcon.asRes(category.iconId)),
                contentDescription = null,
            )
        },
        modifier = modifier,
        onClick = if (onClick != null) {
            { onClick(category.id.toString()) }
        } else {
            null
        },
    )
}