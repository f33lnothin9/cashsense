package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import java.time.LocalDateTime

class LocalDateTimeConverter {

    @TypeConverter
    fun fromLocalDateTime(localDate: LocalDateTime): String {
        return localDate.toString()
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        return value.let { LocalDateTime.parse(it) }
    }
}