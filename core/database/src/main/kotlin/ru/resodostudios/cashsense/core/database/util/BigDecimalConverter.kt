package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import java.math.BigDecimal

internal class BigDecimalConverter {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }
}