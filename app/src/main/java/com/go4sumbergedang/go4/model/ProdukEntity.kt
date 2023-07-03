package com.go4sumbergedang.go4.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val idProduk: String,
    val namaProduk: String?,
    val keterangan: String?,
    val terjual: String?,
    val harga: String?,
    val updatedAt: String?,
    val userId: String?,
    val createdAt: String?,
    val kategori: String?,
    val fotoProduk: String?,
    val status: String?
)