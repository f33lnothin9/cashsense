package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import java.time.Instant

class InstantConverter {

    @TypeConverter
    fun fromInstant(instant: Instant): String {
        return instant.toString()
    }

    @TypeConverter
    fun toInstant(value: String): Instant {
        return value.let { Instant.parse(it) }
    }
}