package com.vitordepaula.invbrasil.repository

import com.vitordepaula.invbrasil.dao.ProductDao
import com.vitordepaula.invbrasil.model.Product

class ProductRepository(private val dao: ProductDao) {

    suspend fun getAll(): MutableList<Product> = dao.get()

    suspend fun getLowStock(): MutableList<Product> {
        return dao.get().filter {
            val atual = it.quantidade.toIntOrNull() ?: 0
            val min = it.quantidadeMinima.toIntOrNull() ?: 0
            atual < min
        }.toMutableList()
    }


    suspend fun insert(product: Product) {
        dao.inserir(mutableListOf(product))
    }

    suspend fun update(product: Product) {
        dao.update(product)
    }

    suspend fun delete(id: Int): Int {
        return dao.delete(id)
    }
}