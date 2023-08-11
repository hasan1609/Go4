package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class RestoCartModel(

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("jumlah_produk")
    val jumlahProduk: Int? = null,

    @field:SerializedName("resto")
    val resto: RestoNearModel? = null,

    @field:SerializedName("toko_id")
    val tokoId: String? = null
)