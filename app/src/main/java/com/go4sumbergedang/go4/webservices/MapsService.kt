package com.go4sumbergedang.go4.webservices

import com.go4sumbergedang.go4.model.ResponseRoutes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsService {
    @GET("maps/api/directions/json")
    fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String,
        @Query("key") key: String
    ): Call<ResponseRoutes>
}