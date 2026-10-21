package com.example.model

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercentage: Int,
    val protocol: String,
    val isDedicated: Boolean = false,
    val operatorType: OperatorType = OperatorType.GLOBAL
)

enum class OperatorType {
    GLOBAL,
    BR_VPS,
    FR_VPS,
    AFRICELL,
    UNITEL,
    SOCIAL_NETWORKS
}
