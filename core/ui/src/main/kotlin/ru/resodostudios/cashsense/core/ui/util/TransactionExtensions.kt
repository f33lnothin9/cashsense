package ru.resodostudios.cashsense.core.ui.util

import ru.resodostudios.cashsense.core.model.data.DateType.ALL
import ru.resodostudios.cashsense.core.model.data.DateType.MONTH
import ru.resodostudios.cashsense.core.model.data.DateType.WEEK
import ru.resodostudios.cashsense.core.model.data.DateType.YEAR
import ru.resodostudios.cashsense.core.model.data.FilterableTransactions
import ru.resodostudios.cashsense.core.model.data.FinanceType.EXPENSES
import ru.resodostudios.cashsense.core.model.data.FinanceType.INCOME
import ru.resodostudios.cashsense.core.model.data.FinanceType.NOT_SET
import ru.resodostudios.cashsense.core.model.data.TransactionFilter
import ru.resodostudios.cashsense.core.model.data.TransactionWithCategory
import java.math.BigDecimal.ZERO
import java.time.temporal.WeekFields

fun List<TransactionWithCategory>.applyTransactionFilter(transactionFilter: TransactionFilter): FilterableTransactions {
    val filteredTransactions = filter { transactionCategory ->
        val transaction = transactionCategory.transaction

        val financeTypeMatch = when (transactionFilter.financeType) {
            NOT_SET -> true
            EXPENSES -> transaction.amount < ZERO
            INCOME -> transaction.amount > ZERO
        }

        val dateTypeMatch = when (transactionFilter.dateType) {
            ALL -> true
            WEEK -> {
                val weekOfTransaction = transaction.timestamp.getZonedDateTime()
                    .get(WeekFields.ISO.weekOfWeekBasedYear())
                weekOfTransaction == getCurrentZonedDateTime().get(WeekFields.ISO.weekOfWeekBasedYear())
            }

            MONTH -> {
                val transactionZonedDateTime = transaction.timestamp.getZonedDateTime()
                transactionZonedDateTime.year == transactionFilter.selectedYearMonth.year &&
                        transactionZonedDateTime.monthValue == transactionFilter.selectedYearMonth.monthValue
            }

            YEAR -> transaction.timestamp.getZonedDateTime().year == transactionFilter.selectedYearMonth.year
        }

        financeTypeMatch && dateTypeMatch
    }

    val availableCategories = filteredTransactions
        .mapNotNull { it.category }
        .distinct()

    val filteredByCategories = if (transactionFilter.selectedCategories.isNotEmpty()) {
        filteredTransactions
            .filter { transactionFilter.selectedCategories.contains(it.category) }
    } else filteredTransactions

    return FilterableTransactions(
        transactionsCategories = filteredByCategories,
        availableCategories = availableCategories,
    )
}