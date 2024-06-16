package ru.resodostudios.cashsense.core.database.util

import androidx.room.TypeConverter
import ru.resodostudios.cashsense.core.model.data.StatusType

internal class StatusTypeConverter {

    @TypeConverter
    fun stringToStatus(value: String) = enumValueOf<StatusType>(value)

    @TypeConverter
    fun statusToString(value: StatusType) = value.name
}