package ru.resodostudios.cashsense.core.model.data

import java.time.YearMonth

data class TransactionFilter(
    val selectedCategories: Set<Category>,
    val financeType: FinanceType,
    val dateType: DateType,
    val selectedYearMonth: YearMonth,
)