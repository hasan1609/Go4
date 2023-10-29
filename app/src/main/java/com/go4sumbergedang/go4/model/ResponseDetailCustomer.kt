package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseDetailCustomer(

	@field:SerializedName("data")
	val data: UserWithDetailModel? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
