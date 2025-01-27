package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import java.util.Currency

internal class CurrencyConverter {

    @TypeConverter
    fun currencyToString(currency: Currency?): String? = currency?.currencyCode

    @TypeConverter
    fun stringToCurrency(currencyCode: String?): Currency? = currencyCode?.let(Currency::getInstance)
}