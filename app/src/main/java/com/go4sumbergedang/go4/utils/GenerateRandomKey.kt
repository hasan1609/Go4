package com.go4sumbergedang.go4.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object GenerateRandomKey {
    fun generateKeyFromDatetime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val key = currentDateTime.format(formatter)

        val hash = generateRandomString(8)
        return "$key$hash"
    }

    private fun generateRandomString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
