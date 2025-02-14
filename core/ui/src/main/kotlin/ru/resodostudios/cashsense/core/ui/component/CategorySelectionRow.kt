package ru.resodostudios.cashsense.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.ContextualFlowRowOverflow
import androidx.compose.foundation.layout.ContextualFlowRowOverflowScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.designsystem.icon.outlined.Check
import ru.resodostudios.cashsense.core.designsystem.theme.CsTheme
import ru.resodostudios.cashsense.core.model.data.Category
import ru.resodostudios.cashsense.core.ui.CategoryPreviewParameterProvider
import ru.resodostudios.cashsense.core.locales.R as localesR

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategorySelectionRow(
    availableCategories: List<Category>,
    selectedCategories: Set<Category>,
    onCategorySelect: (Category) -> Unit,
    onCategoryDeselect: (Category) -> Unit,
    modifier: Modifier = Modifier,
    defaultMaxLines: Int = 3,
) {
    var maxLines by rememberSaveable { mutableIntStateOf(defaultMaxLines) }

    val moreOrCollapseIndicator = @Composable { scope: ContextualFlowRowOverflowScope ->
        val remainingItems = availableCategories.size - scope.shownItemCount

        FilterChip(
            selected = false,
            onClick = { maxLines = if (remainingItems == 0) defaultMaxLines else Int.MAX_VALUE },
            label = {
                Text(
                    text = if (remainingItems == 0) stringResource(localesR.string.show_less) else "+$remainingItems",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }

    ContextualFlowRow(
        modifier = modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(align = Alignment.Top),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        itemCount = availableCategories.size,
        maxLines = maxLines,
        overflow = ContextualFlowRowOverflow.expandOrCollapseIndicator(
            minRowsToShowCollapse = defaultMaxLines + 1,
            expandIndicator = moreOrCollapseIndicator,
            collapseIndicator = moreOrCollapseIndicator,
        ),
    ) { index ->
        val category = runCatching { availableCategories[index] }.getOrDefault(Category())

        CategoryChip(
            selected = selectedCategories.contains(category),
            category = category,
            onClick = {
                if (selectedCategories.contains(category)) {
                    onCategoryDeselect(category)
                } else {
                    onCategorySelect(category)
                }
            },
        )
    }
}

@Composable
private fun CategoryChip(
    selected: Boolean,
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val icon = if (selected) CsIcons.Outlined.Check else StoredIcon.asImageVector(category.iconId)

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.title.toString()) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = modifier.size(FilterChipDefaults.IconSize),
            )
        }
    )
}

@Preview
@Composable
private fun CategorySelectorRowPreview(
    @PreviewParameter(CategoryPreviewParameterProvider::class)
    categories: List<Category>,
) {
    CsTheme {
        Surface {
            CategorySelectionRow(
                availableCategories = categories,
                selectedCategories = setOf(categories.first()),
                onCategorySelect = {},
                onCategoryDeselect = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}