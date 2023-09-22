package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ReviewModel(

	@field:SerializedName("resto_id")
	val restoId: String? = null,

	@field:SerializedName("driver_id")
	val driverId: String? = null,

	@field:SerializedName("ulasan_driver")
	val ulasanDriver: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("rating_resto")
	val ratingResto: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id_review")
	val idReview: String? = null,

	@field:SerializedName("customer_id")
	val customerId: String? = null,

	@field:SerializedName("rating_driver")
	val ratingDriver: Double? = null,

	@field:SerializedName("ulasan_resto")
	val ulasanResto: String? = null
)
