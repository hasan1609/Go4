package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.ResponseProduk
import com.go4sumbergedang.go4.model.ResponseRestoTerdekat
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("nearby/resto/{latitude}&{longitude}")
    fun getRestoTerdekat(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseRestoTerdekat>

    @GET("resto/produk/{id}")
    fun getProdukByIdResto(
        @Path("id") id: String,
    ): Call<ResponseProduk>
}