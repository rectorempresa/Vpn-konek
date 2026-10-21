package com.example.model

data class VoucherCode(
    val code: String,
    val durationMinutes: Long,
    val timeLabel: String,
    var isUsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
