package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseResto(

	@field:SerializedName("data")
	val data: List<RestoModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

