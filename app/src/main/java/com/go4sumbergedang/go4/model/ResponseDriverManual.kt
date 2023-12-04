package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseDriverManual(

	@field:SerializedName("data")
	val data: List<DataDriverManual?>? = null
)

data class DataDriverManual(

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("distance")
	val distance: Float? = null,

	@field:SerializedName("driver")
	val driver: DriverModel? = null
)
