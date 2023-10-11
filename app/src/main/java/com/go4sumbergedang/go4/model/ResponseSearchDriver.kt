package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseSearchDriver(

	@field:SerializedName("data")
	val data: List<DriverModel?>? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

