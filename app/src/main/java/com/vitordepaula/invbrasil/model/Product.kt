package com.vitordepaula.invbrasil.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_product")
data class Product (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "nome") val nome: String,
    @ColumnInfo(name = "quantidade") val quantidade: String
)