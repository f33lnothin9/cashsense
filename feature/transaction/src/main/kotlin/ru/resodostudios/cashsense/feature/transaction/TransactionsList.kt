package ru.resodostudios.cashsense.feature.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.resodostudios.cashsense.core.model.data.Currency
import ru.resodostudios.cashsense.core.model.data.Transaction
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import ru.resodostudios.cashsense.core.ui.EditAndDeleteDropdownMenu
import ru.resodostudios.cashsense.core.ui.formattedDate
import ru.resodostudios.cashsense.core.ui.getFormattedAmountAndCurrency
import ru.resodostudios.cashsense.core.ui.R as uiR
import ru.resodostudios.cashsense.feature.categories.R as categoriesR

fun LazyListScope.transactions(
    transactionsWithCategories: List<TransactionWithCategory>,
    currency: Currency,
    onEdit: (TransactionWithCategory) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    var currentDate: String? = null
    val sortedTransactions = transactionsWithCategories.sortedByDescending { it.transaction.date }

    items(sortedTransactions) { transactionWithCategory ->
        val category = transactionWithCategory.category
        Column {
            if (currentDate != formattedDate(date = transactionWithCategory.transaction.date)) {
                currentDate = formattedDate(date = transactionWithCategory.transaction.date)
                Text(
                    text = currentDate!!,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            ListItem(
                headlineContent = {
                    Text(
                        text = getFormattedAmountAndCurrency(
                            amount = transactionWithCategory.transaction.amount,
                            currencyName = currency.name
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    EditAndDeleteDropdownMenu(
                        onEdit = { onEdit(transactionWithCategory) },
                        onDelete = { onDelete(transactionWithCategory.transaction) }
                    )
                },
                supportingContent = {
                    Text(
                        text = if (transactionWithCategory.category?.title == null) stringResource(
                            uiR.string.none
                        ) else transactionWithCategory.category!!.title.toString()
                    )
                },
                leadingContent = {
                    Icon(
                        painter = if (category?.icon != null) {
                            painterResource(category.icon!!)
                        } else {
                            painterResource(categoriesR.drawable.ic_outlined_receipt_long)
                        },
                        contentDescription = null
                    )
                }
            )
        }
    }
}