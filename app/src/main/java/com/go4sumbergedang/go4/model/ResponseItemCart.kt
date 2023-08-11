package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseItemCart(

	@field:SerializedName("data")
	val data: List<ItemCartModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("totalJumlah")
	val totalJumlah: Int? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

