package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.ResponseResto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("nearby/resto/{latitude}&{longitude}")
    fun getRestoTerdekat(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseResto>

    @GET("resto/produk/{id}/{lat}&{long}")
    fun getProdukByIdResto(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseResto>
}