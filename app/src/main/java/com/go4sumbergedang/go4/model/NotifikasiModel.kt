package com.go4sumbergedang.go4.model

data class NotificationData(
    val to: String,
    val notification: Notification,

)

data class Notification(
    val title: String,
    val body: String
)