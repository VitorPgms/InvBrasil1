package com.vitordepaula.invbrasil.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale")
data class Sale (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nameClient: String,
    val address: String,
    val productId: Int,
    val nameProduct: String,
    val quantitySale: Int,
    val dataSale: String
    )