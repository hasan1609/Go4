package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class OngkirModel(

    @field:SerializedName("harga")
    val harga: Int? = null,

    @field:SerializedName("jarak")
    val jarak: Int? = null,

    @field:SerializedName("waktu")
    val waktu: Int? = null,

    @field:SerializedName("jenis_kendaraan")
    val jenisKendaraan: String? = null
)