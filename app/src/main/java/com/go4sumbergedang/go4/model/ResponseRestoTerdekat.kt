package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class ResponseRestoTerdekat(

	@field:SerializedName("data")
	val data: List<DetailRestoTerdekatModel?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class DetailRestoTerdekatModel(

	@field:SerializedName("jam_tutup")
	val jamTutup: String? = null,

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("nama_resto")
	val namaResto: String? = null,

	@field:SerializedName("status_akun")
	val statusAkun: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("ttl")
	val ttl: String? = null,

	@field:SerializedName("status_toko")
	val statusToko: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null,

	@field:SerializedName("jam_buka")
	val jamBuka: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("id_detail")
	val idDetail: String? = null,

	@field:SerializedName("tempat_lahir")
	val tempatLahir: String? = null,

	@field:SerializedName("foto")
	val foto: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("user")
	val user: UserModel? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
)

data class UserModel(

	@field:SerializedName("fcm")
	val fcm: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

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
