package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseItemCart(

	@field:SerializedName("ongkir")
	val ongkir: Int? = null,

	@field:SerializedName("data")
	val data: List<ItemCartModel?>? = null,

	@field:SerializedName("jarak")
	val jarak: Any? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("totalJumlah")
	val totalJumlah: Int? = null,

	@field:SerializedName("totalAll")
	val totalAll: Int? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

