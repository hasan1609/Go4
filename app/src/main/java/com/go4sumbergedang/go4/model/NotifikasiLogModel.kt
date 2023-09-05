package com.go4sumbergedang.go4.model

import com.google.gson.annotations.SerializedName

data class NotifikasiLogModel(

    @field:SerializedName("data")
    val data: String? = null,

    @field:SerializedName("updated_at")
    val updatedAt: String? = null,

    @field:SerializedName("recive_id")
    val reciveId: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("judul")
    val judul: String? = null,

    @field:SerializedName("body")
    val body: String? = null,

    @field:SerializedName("id_notification_log")
    val idNotificationLog: String? = null,

    @field:SerializedName("sender_id")
    val senderId: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)