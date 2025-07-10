package com.vitordepaula.invbrasil.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vitordepaula.invbrasil.model.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT * FROM tabela_category ORDER BY nome ASC")
    suspend fun getAll(): List<Category>

    @Update
    suspend fun update(category: Category)
}