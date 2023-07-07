package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class CartModel(

    @field:SerializedName("id_produk")
    val idProduk: String? = null,

    @field:SerializedName("nama_produk")
    val namaProduk: String? = null,

    @field:SerializedName("harga")
    val harga: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("kategori")
    val kategori: String? = null,

    @field:SerializedName("foto_produk")
    val fotoProduk: String? = null,

    @field:SerializedName("junlah")
    var jumlah: Int? = null,

    @field:SerializedName("catatan")
    val catatan: String? = null

)