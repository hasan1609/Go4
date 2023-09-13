package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

// cari resto terderkat
    @GET("nearby/resto/{latitude}&{longitude}")
    fun getRestoTerdekat(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseResto>
// get data resto by id
    @GET("resto/{id}")
    fun getRestoById(
        @Path("id") id: String,
    ): Call<ResponseRestoSingle>
// get produk by id resto
    @GET("resto/produk/{id}/{lat}&{long}")
    fun getProdukByIdResto(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseResto>
//    add to cart
    @FormUrlEncoded
    @POST("cart")
    fun addToCart(
        @Field("user_id") user_id: String,
        @Field("produk_id") produk_id: String,
        @Field("toko_id") toko_id: String,
        @Field("jumlah") jumlah: String,
        @Field("catatan") catatan: String,
        @Field("harga") harga: String,
    ): Call<ResponsePostData>
//    get cart Count
    @GET("cart/count/{id}")
    fun getCountCart(
        @Path("id") id: String,
    ): Call<ResponseCountCart>
//    get cart
    @GET("cart/{id}/{lat}&{long}")
    fun getCart(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseCart>
//    get item cart
    @GET("cart/item/{id}/{user}")
    fun getItemCart(
        @Path("id") id: String,
        @Path("user") user: String,
    ): Call<ResponseItemCart>
//    hapus cart by id resto
    @POST("cart/{id}/{user}")
    fun hapusCart(
        @Path("id") id: String,
        @Path("user") user: String
    ): Call<ResponsePostData>
//    hapus cart by id cart
    @POST("item/cart/{id}")
    fun hapusByIdCart(
        @Path("id") id: String
    ): Call<ResponsePostData>

    @GET("order/customer/{id}")
    fun getOrderLog(
        @Path("id") id: String
    ): Call<ResponseOrderLog>


    @GET("notifikasi/{id}")
    fun getNotifikasiLog(
        @Path("id") id: String
    ): Call<ResponseNotifikasiLog>

    @POST("notifikasi/{id}")
    fun updateNotifikasiStatusLog(
        @Path("id") id: String
    ): Call<ResponsePostData>


}