package ru.resodostudios.cashsense.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.model.TransactionEntity


@Database(
    entities = [
        TransactionEntity::class
    ],
    version = 1,
    exportSchema = false,
)
abstract class CsDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
}