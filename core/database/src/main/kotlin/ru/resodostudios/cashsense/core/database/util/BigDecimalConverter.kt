package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import java.math.BigDecimal

internal class BigDecimalConverter {

    @TypeConverter
    fun bigDecimalToString(value: BigDecimal?): String? = value?.toString()

    @TypeConverter
    fun stringToBigDecimal(value: String?): BigDecimal? = value?.let(::BigDecimal)
}