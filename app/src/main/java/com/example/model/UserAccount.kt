package com.example.model

data class UserAccount(
    val id: String,
    val username: String,
    val email: String,
    val phone: String = "",
    val password: String,
    var remainingTimeSeconds: Long = 300L, // 5 min inicial de teste para novas contas
    var isVip: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val usedCoupons: MutableList<String> = mutableListOf()
)
