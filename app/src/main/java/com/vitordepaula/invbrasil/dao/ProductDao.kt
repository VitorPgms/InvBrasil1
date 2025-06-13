package com.vitordepaula.invbrasil.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.vitordepaula.invbrasil.model.Product

@Dao
interface ProductDao {

    @Insert
    fun inserir(listProduct: MutableList<Product>)

    @Query("SELECT * FROM tabela_product ORDER BY nome ASC")
    fun get(): MutableList<Product>

    @Update
    fun update(product: Product)

    @Query("DELETE FROM tabela_product WHERE uid = :id ")
    suspend fun delete(id: Int):Int
}