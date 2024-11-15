package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import kotlin.uuid.Uuid

internal class UuidConverter {

    @TypeConverter
    fun uuidToString(value: Uuid?): String? = value?.toString()

    @TypeConverter
    fun stringToUuid(value: String?): Uuid? = value?.let(Uuid::parse)
}