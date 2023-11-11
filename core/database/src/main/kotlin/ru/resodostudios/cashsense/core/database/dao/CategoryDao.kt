package ru.resodostudios.cashsense.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.resodostudios.cashsense.core.database.model.CategoryEntity

@Dao
interface CategoryDao {

    @Query(
        value = """
        SELECT * FROM categories
        WHERE id = :categoryId
    """,
    )
    fun getCategoryEntity(categoryId: String): Flow<CategoryEntity>

    @Query(value = "SELECT * FROM categories ORDER BY id DESC")
    fun getCategoryEntities(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun upsertCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)
}