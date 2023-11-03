package com.go4sumbergedang.go4.model

data class ChatModel(
    val sender: String? = null,
    val message: String? = null,
    val receiver: String? = null,
    val isseen: Boolean = false,
    val url: String? = null,
    val messageId: String? = null,
)