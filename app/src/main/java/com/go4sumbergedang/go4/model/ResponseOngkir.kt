package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseOngkir(

	@field:SerializedName("data")
	val data: List<OngkirModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
