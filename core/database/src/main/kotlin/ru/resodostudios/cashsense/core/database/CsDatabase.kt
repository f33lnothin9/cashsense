package ru.resodostudios.cashsense.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.resodostudios.cashsense.core.database.dao.CategoryDao
import ru.resodostudios.cashsense.core.database.dao.SubscriptionDao
import ru.resodostudios.cashsense.core.database.dao.TransactionDao
import ru.resodostudios.cashsense.core.database.dao.WalletDao
import ru.resodostudios.cashsense.core.database.model.CategoryEntity
import ru.resodostudios.cashsense.core.database.model.SubscriptionEntity
import ru.resodostudios.cashsense.core.database.model.TransactionCategoryCrossRefEntity
import ru.resodostudios.cashsense.core.database.model.TransactionEntity
import ru.resodostudios.cashsense.core.database.model.WalletEntity
import ru.resodostudios.cashsense.core.database.util.BigDecimalConverter
import ru.resodostudios.cashsense.core.database.util.InstantConverter

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        WalletEntity::class,
        SubscriptionEntity::class,
        TransactionCategoryCrossRefEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    InstantConverter::class,
    BigDecimalConverter::class,
)
abstract class CsDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun categoryDao(): CategoryDao

    abstract fun walletDao(): WalletDao

    abstract fun subscriptionDao(): SubscriptionDao
}