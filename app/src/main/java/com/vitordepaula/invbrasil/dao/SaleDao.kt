package com.vitordepaula.invbrasil.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vitordepaula.invbrasil.model.Sale

@Dao
interface SaleDao {
    @Insert
    suspend fun insert(sale: Sale)

    @Query("SELECT * FROM sale ORDER BY dataSale DESC")
    suspend fun listAll(): List<Sale>
}