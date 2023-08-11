package com.go4sumbergedang.go4.utils

import com.go4sumbergedang.go4.model.ResponseCountCart
import com.go4sumbergedang.go4.webservices.ApiClient
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CartUtils {
    private val api = ApiClient.instance()

    fun getCartItemCount(idUser: String) {
        api.getCountCart(idUser).enqueue(object : Callback<ResponseCountCart> {
            override fun onResponse(call: Call<ResponseCountCart>, response: Response<ResponseCountCart>) {
                if (response.isSuccessful) {
                    val itemCount = response.body()?.data ?: 0
                    EventBus.getDefault().post(CartItemCountEvent(itemCount)) // Post event ke EventBus
                }
            }

            override fun onFailure(call: Call<ResponseCountCart>, t: Throwable) {
                // Tangani kesalahan jika diperlukan
            }
        })
    }
}