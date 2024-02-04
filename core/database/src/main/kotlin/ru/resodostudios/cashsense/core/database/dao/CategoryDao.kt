package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.CategoryEntity

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories WHERE id = :id")
    fun getCategoryEntity(id: String): Flow<CategoryEntity>

    @Query("SELECT * FROM categories ORDER BY id DESC")
    fun getCategoryEntities(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}