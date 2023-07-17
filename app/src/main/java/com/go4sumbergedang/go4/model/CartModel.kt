package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class CartModel(
    @field:SerializedName("id_produk")
    var idProduk: String? = null,

    @field:SerializedName("nama_produk")
    var namaProduk: String? = null,

    @field:SerializedName("harga")
    var harga: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("kategori")
    var kategori: String? = null,

    @field:SerializedName("keterangan")
    var keterangan: String? = null,

    @field:SerializedName("foto_produk")
    var fotoProduk: String? = null,

    @field:SerializedName("junlah")
    var jumlah: Int? = null,

    @field:SerializedName("catatan")
    var catatan: String? = null
)