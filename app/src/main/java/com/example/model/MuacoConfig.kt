package com.example.model

import org.json.JSONObject
import java.util.UUID

/**
 * MuacoConfig
 * 
 * Modelo de dados para o arquivo de configuração proprietário com extensão .muaco
 * Desenvolvido para o app Konek VPN Pro (Isaac Muaco).
 * Contém parâmetros de VPS, SSH, SNI, Payload e Proteção para entregar internet
 * funcional de verdade sem erros 404.
 */
data class MuacoConfig(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val title: String,
    val description: String = "",
    val vpsHost: String,
    val vpsPort: Int = 443,
    val connectionMode: String = MODE_SSH_SSL_SNI,
    val sniHost: String = "portal.unitel.ao",
    val sshUser: String = "muaco_user",
    val sshPassword: String = "",
    val customPayload: String = "",
    val dnsPrimary: String = "1.1.1.1",
    val dnsSecondary: String = "8.8.8.8",
    val isLocked: Boolean = false,
    val lockPassword: String = "",
    val expirationDate: String = "Ilimitado",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
) {
    companion object {
        const val EXTENSION = ".muaco"
        const val MODE_SSH_SSL_SNI = "SSH + SSL/TLS (SNI)"
        const val MODE_SSH_DIRECT = "SSH Direct"
        const val MODE_SSH_WEBSOCKET = "SSH + WebSocket"
        const val MODE_SSL_DIRECT = "SSL / TLS Direct SNI"

        val ALL_MODES = listOf(
            MODE_SSH_SSL_SNI,
            MODE_SSH_DIRECT,
            MODE_SSH_WEBSOCKET,
            MODE_SSL_DIRECT
        )

        /**
         * Converte uma string bruta (JSON ou Base64) de um arquivo .muaco para MuacoConfig
         */
        fun fromMuacoString(rawContent: String): MuacoConfig? {
            return try {
                var cleaned = rawContent.trim()
                if (cleaned.startsWith("MUACO#")) {
                    cleaned = cleaned.removePrefix("MUACO#")
                    val decodedBytes = android.util.Base64.decode(cleaned, android.util.Base64.DEFAULT)
                    cleaned = String(decodedBytes, Charsets.UTF_8)
                }

                val json = JSONObject(cleaned)
                var fName = json.optString("fileName", "config.muaco")
                if (!fName.endsWith(EXTENSION)) {
                    fName += EXTENSION
                }

                MuacoConfig(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    fileName = fName,
                    title = json.optString("title", "Configuração .muaco"),
                    description = json.optString("description", ""),
                    vpsHost = json.optString("vpsHost", "102.130.112.5"),
                    vpsPort = json.optInt("vpsPort", 443),
                    connectionMode = json.optString("connectionMode", MODE_SSH_SSL_SNI),
                    sniHost = json.optString("sniHost", "portal.unitel.ao"),
                    sshUser = json.optString("sshUser", "muaco_user"),
                    sshPassword = json.optString("sshPassword", ""),
                    customPayload = json.optString("customPayload", ""),
                    dnsPrimary = json.optString("dnsPrimary", "1.1.1.1"),
                    dnsSecondary = json.optString("dnsSecondary", "8.8.8.8"),
                    isLocked = json.optBoolean("isLocked", false),
                    lockPassword = json.optString("lockPassword", ""),
                    expirationDate = json.optString("expirationDate", "Ilimitado"),
                    createdAt = json.optLong("createdAt", System.currentTimeMillis()),
                    isActive = json.optBoolean("isActive", false)
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Serializa o objeto MuacoConfig para JSON
     */
    fun toJsonString(): String {
        val json = JSONObject()
        json.put("app", "Konek VPN Pro")
        json.put("creator", "Isaac Muaco")
        json.put("format", "MUACO_VPN_CONFIG_V1")
        json.put("id", id)
        json.put("fileName", if (fileName.endsWith(EXTENSION)) fileName else "$fileName$EXTENSION")
        json.put("title", title)
        json.put("description", description)
        json.put("vpsHost", vpsHost)
        json.put("vpsPort", vpsPort)
        json.put("connectionMode", connectionMode)
        json.put("sniHost", sniHost)
        json.put("sshUser", sshUser)
        json.put("sshPassword", sshPassword)
        json.put("customPayload", customPayload)
        json.put("dnsPrimary", dnsPrimary)
        json.put("dnsSecondary", dnsSecondary)
        json.put("isLocked", isLocked)
        json.put("lockPassword", lockPassword)
        json.put("expirationDate", expirationDate)
        json.put("createdAt", createdAt)
        json.put("isActive", isActive)
        return json.toString(2)
    }

    /**
     * Serializa para o formato exportável codificado em Base64 com cabeçalho MUACO#
     */
    fun toExportableMuacoPayload(): String {
        val jsonStr = toJsonString()
        val base64 = android.util.Base64.encodeToString(jsonStr.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
        return "MUACO#$base64"
    }

    /**
     * Gera o nome de arquivo seguro garantindo a extensão .muaco
     */
    fun getNormalizedFileName(): String {
        val sanitized = fileName.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return if (sanitized.endsWith(EXTENSION, ignoreCase = true)) sanitized else "$sanitized$EXTENSION"
    }
}
