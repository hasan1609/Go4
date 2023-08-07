package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.NotificationData
import com.go4sumbergedang.go4.model.ResponseResto
import com.go4sumbergedang.go4.model.ResponseRestoSingle
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers(
        "Content-Type: application/json",
        "Authorization: key=AAAARdKDCeY:APA91bHuDQMn4QILQ8-zvcC8zp2et2N1Ii9dvBxqbz-l_pYF3iw5NjlVTQF3eYEUavNi_L0rpfq5FBoGWs-O9hmLBxqi5cl76AKbYhppt2nSGKG0DWldgb461Sp-jzBUztfSLiKsyFvl" // Ganti dengan kunci server FCM Anda
    )
    @POST("fcm/send")
    fun sendNotification(@Body notification: NotificationData): Call<ResponseBody>

    @GET("nearby/resto/{latitude}&{longitude}")
    fun getRestoTerdekat(
        @Path("latitude") latitude: String,
        @Path("longitude") longitude: String
    ): Call<ResponseResto>

    @GET("resto/{id}")
    fun getRestoById(
        @Path("id") id: String,
    ): Call<ResponseRestoSingle>

    @GET("resto/produk/{id}/{lat}&{long}")
    fun getProdukByIdResto(
        @Path("id") id: String,
        @Path("lat") lat: String,
        @Path("long") long: String,
    ): Call<ResponseResto>
}