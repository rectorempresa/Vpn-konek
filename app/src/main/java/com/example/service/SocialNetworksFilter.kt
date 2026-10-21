package com.example.service

/**
 * SocialNetworksFilter
 * 
 * Regras estritas de Firewall para o modo "Servidor de Rede Social":
 * Conforme solicitado:
 * - Se o servidor for de Rede Social: Bloqueia toda a internet externa e funciona
 *   ESTRITAMENTE para: Facebook, Instagram, Telegram, TikTok e YouTube.
 *   Todo o resto da internet é bloqueado (Firewall e DNS).
 * - Se for para Internet Zero Data (Unitel Zero Saldo, etc.) ou servidores Globais:
 *   A internet é 100% GLOBAL e livre para todos os aplicativos e sites.
 */
object SocialNetworksFilter {

    // Lista autorizada de domínios e CDNs para Facebook, Instagram, Telegram, TikTok e YouTube
    val ALLOWED_SOCIAL_DOMAINS = listOf(
        // 1. FACEBOOK & MESSENGER
        "facebook.com",
        "fbcdn.net",
        "fbsbx.com",
        "fb.me",
        "fb.com",
        "messenger.com",
        "facebook.net",
        "m.facebook.com",
        "graph.facebook.com",
        "touch.facebook.com",
        "free.facebook.com",
        "b-graph.facebook.com",

        // 2. INSTAGRAM
        "instagram.com",
        "cdninstagram.com",
        "ig.me",
        "i.instagram.com",

        // 3. TELEGRAM
        "telegram.org",
        "t.me",
        "telegra.ph",
        "telegram.me",
        "telesco.pe",
        "api.telegram.org",

        // 4. TIKTOK
        "tiktok.com",
        "tiktokv.com",
        "tiktokcdn.com",
        "byteoversea.com",
        "ibytedtos.com",
        "ibyteimg.com",
        "musically.com",

        // 5. YOUTUBE
        "youtube.com",
        "youtu.be",
        "ytimg.com",
        "googlevideo.com",
        "youtubei.googleapis.com",
        "yt3.ggpht.com",
        "ggpht.com",
        "widevine.com",

        // Localhost / Diagnóstico interno do app
        "localhost",
        "127.0.0.1"
    )

    /**
     * Verifica se o host de destino é permitido quando o modo de Redes Sociais está ativo.
     * @param host Host ou IP (ex: "m.facebook.com", "api.tiktokv.com", "globo.com")
     * @return true se pertencer a Facebook, Instagram, Telegram, TikTok ou YouTube; false caso contrário.
     */
    fun isHostAllowedInSocialMode(host: String): Boolean {
        if (host.isBlank()) return false
        val cleanHost = host.lowercase().trim().removeSuffix(".")

        // Se for IP direto, checagem rápida de loopback
        if (cleanHost == "127.0.0.1" || cleanHost == "localhost") return true

        for (allowed in ALLOWED_SOCIAL_DOMAINS) {
            if (cleanHost == allowed || cleanHost.endsWith(".$allowed")) {
                return true
            }
        }
        return false
    }

    /**
     * Extrai o nome de domínio a partir dos bytes de uma consulta DNS (RFC 1035).
     */
    fun extractDnsQueryDomain(dnsQuery: ByteArray): String? {
        try {
            if (dnsQuery.size < 13) return null
            var offset = 12 // O cabeçalho DNS tem 12 bytes
            val domain = StringBuilder()

            while (offset < dnsQuery.size) {
                val len = dnsQuery[offset].toInt() and 0xFF
                if (len == 0) break // Fim do QNAME
                if (offset + 1 + len > dnsQuery.size) return null

                if (domain.isNotEmpty()) domain.append(".")
                domain.append(String(dnsQuery, offset + 1, len, Charsets.US_ASCII))
                offset += 1 + len
            }
            return if (domain.isNotEmpty()) domain.toString().lowercase() else null
        } catch (_: Exception) {
            return null
        }
    }
}
