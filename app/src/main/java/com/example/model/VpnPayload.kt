package com.example.model

data class VpnPayload(
    val id: String,
    val name: String,
    val operatorType: OperatorType,
    val badgeTitle: String,
    val sniHost: String,
    val customHeader: String,
    val description: String,
    val isFreezeData: Boolean = false,
    val isZeroBalance: Boolean = false
)
