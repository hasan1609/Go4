package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseCart(

	@field:SerializedName("data")
	val data: List<RestoCartModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
