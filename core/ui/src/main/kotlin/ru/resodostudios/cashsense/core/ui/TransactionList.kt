package ru.resodostudios.cashsense.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import ru.resodostudios.cashsense.core.designsystem.icon.CsIcons
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import java.time.temporal.ChronoUnit

fun LazyGridScope.transactions(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: String,
    onEdit: (String) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    val sortedTransactionsAndCategories =
        transactionsWithCategories
            .sortedByDescending { it.transaction.date }
            .groupBy { it.transaction.date.toJavaInstant().truncatedTo(ChronoUnit.DAYS) }
            .toSortedMap(compareByDescending { it })
    val groupedTransactionsAndCategories = sortedTransactionsAndCategories.map {
        Pair(
            it.key,
            it.value
        )
    }
    groupedTransactionsAndCategories.forEach { group ->
        item(
            span = { GridItemSpan(maxLineSpan) }
        ) {
            Text(
                text = formattedDate(group.first.toKotlinInstant()),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(
            items = group.second,
            key = { it.transaction.id },
            contentType = { "transactionWithCategory" }
        ) { transactionWithCategory ->
            val category = transactionWithCategory.category
            ListItem(
                headlineContent = {
                    Text(
                        text = getFormattedAmountAndCurrency(
                            amount = transactionWithCategory.transaction.amount,
                            currencyName = currency
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    EditAndDeleteDropdownMenu(
                        onEdit = { onEdit(transactionWithCategory.transaction.id) },
                        onDelete = { onDelete(transactionWithCategory.transaction) }
                    )
                },
                supportingContent = {
                    Text(
                        text = category?.title ?: stringResource(R.string.none),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            category?.iconRes ?: CsIcons.Transaction
                        ),
                        contentDescription = null
                    )
                }
            )
        }
    }
}