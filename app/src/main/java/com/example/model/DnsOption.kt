package com.example.model

data class DnsOption(
    val id: String,
    val name: String,
    val primaryIp: String,
    val secondaryIp: String,
    val description: String,
    val isCustom: Boolean = false
)
