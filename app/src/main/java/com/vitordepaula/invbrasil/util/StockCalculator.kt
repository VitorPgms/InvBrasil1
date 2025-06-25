package com.vitordepaula.invbrasil.util

import com.vitordepaula.invbrasil.model.Product

object StockCalculator {

    fun calculateTotal(product: List<Product>): Pair<Int, Double> {
        var totalQuantity = 0
        var totalAmount = 0.0

        product.forEach{ product ->
            val qtd = product.quantidade.toIntOrNull() ?: 0
            val price = product.preco.replace(",",".").toDoubleOrNull() ?: 0.0

            totalQuantity += qtd
            totalAmount += qtd * price
        }

        return Pair(totalQuantity, totalAmount)
    }

}