package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class UserWithDetailModel(

    @field:SerializedName("fcm")
    val fcm: String? = null,

    @field:SerializedName("role")
    val role: String? = null,

    @field:SerializedName("nama")
    val nama: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("detail_customer")
    val detailCustomer: DetailCustomerModel? = null,

    @field:SerializedName("tlp")
    val tlp: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("email_verified_at")
    val emailVerifiedAt: Any? = null,

    @field:SerializedName("id_user")
    val idUser: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)