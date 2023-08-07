package com.go4sumbergedang.go4.model

data class TokoCartModel(
    val idToko: String? = null,
    var foto: String? = null,
    var nama_toko: String? = null,
    var cartItems: MutableMap<String, CartModel>? = null
)