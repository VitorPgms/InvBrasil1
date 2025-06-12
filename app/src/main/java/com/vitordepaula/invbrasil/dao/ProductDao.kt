package com.vitordepaula.invbrasil.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.vitordepaula.invbrasil.model.Product

@Dao
interface ProductDao {

    @Insert
    fun inserir(listProduct: MutableList<Product>)

    @Query("SELECT * FROM tabela_product ORDER BY nome ASC")
    fun get(): MutableList<Product>

    @Query("UPDATE tabela_product SET nome = :novoNome, quantidade = :novaQuantidade " +
    "WHERE uid = :id")
    fun update(id: Int, novoNome: String, novaQuantidade: String)

    @Query("DELETE FROM tabela_product WHERE uid = :id ")
    fun delete(id: Int){

    }
}