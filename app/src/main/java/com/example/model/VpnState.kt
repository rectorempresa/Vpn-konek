package com.example.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    AUTHENTICATING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

data class VpnStats(
    val uploadBytes: Long = 0L,
    val downloadBytes: Long = 0L,
    val uploadSpeedBps: Long = 0L,
    val downloadSpeedBps: Long = 0L,
    val durationSeconds: Long = 0L,
    val assignedIp: String = "10.8.0.2",
    val cipher: String = "AES-256-GCM (WireGuard TLS 1.3)",
    val pingMs: Int = 24
)
