package com.example.data

import com.example.model.OperatorType
import com.example.model.VpnPayload
import com.example.model.VpnServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

object VpnRepository {

    private val _isListUpdated = MutableStateFlow(true)
    val isListUpdated: StateFlow<Boolean> = _isListUpdated.asStateFlow()

    private val _availableServers by lazy { MutableStateFlow<List<VpnServer>>(fullServerList) }
    val availableServers: StateFlow<List<VpnServer>> get() = _availableServers.asStateFlow()

    private val _availablePayloads by lazy { MutableStateFlow<List<VpnPayload>>(fullPayloadList) }
    val availablePayloads: StateFlow<List<VpnPayload>> get() = _availablePayloads.asStateFlow()

    private val _selectedServer by lazy { MutableStateFlow<VpnServer?>(fullServerList[0]) }
    val selectedServer: StateFlow<VpnServer?> get() = _selectedServer.asStateFlow()

    private val _selectedPayload by lazy { MutableStateFlow<VpnPayload?>(fullPayloadList[0]) }
    val selectedPayload: StateFlow<VpnPayload?> get() = _selectedPayload.asStateFlow()

    val fullServerList = listOf(
        VpnServer(
            id = "au-sydney-01",
            name = "Australia",
            country = "Australia",
            countryCode = "AU",
            flagEmoji = "🇦🇺",
            ipAddress = "139.130.4.5",
            pingMs = 28,
            loadPercentage = 32,
            protocol = "WireGuard / Konek UDP",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "al-tirana-01",
            name = "Albania",
            country = "Albania",
            countryCode = "AL",
            flagEmoji = "🇦🇱",
            ipAddress = "80.78.64.12",
            pingMs = 42,
            loadPercentage = 18,
            protocol = "ChaCha20 / TLS",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "ar-buenosaires-01",
            name = "Argentina",
            country = "Argentina",
            countryCode = "AR",
            flagEmoji = "🇦🇷",
            ipAddress = "181.30.128.9",
            pingMs = 54,
            loadPercentage = 25,
            protocol = "WireGuard UDP",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "at-vienna-01",
            name = "Austria",
            country = "Austria",
            countryCode = "AT",
            flagEmoji = "🇦🇹",
            ipAddress = "193.170.0.1",
            pingMs = 38,
            loadPercentage = 21,
            protocol = "TLS / Direct",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "by-minsk-01",
            name = "Belarus",
            country = "Belarus",
            countryCode = "BY",
            flagEmoji = "🇧🇾",
            ipAddress = "178.124.128.5",
            pingMs = 48,
            loadPercentage = 19,
            protocol = "ShadowSocks / UDP",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "be-brussels-01",
            name = "Belgium",
            country = "Belgium",
            countryCode = "BE",
            flagEmoji = "🇧🇪",
            ipAddress = "195.238.0.1",
            pingMs = 35,
            loadPercentage = 29,
            protocol = "WireGuard UDP",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "ba-sarajevo-01",
            name = "Bosnia",
            country = "Bosnia",
            countryCode = "BA",
            flagEmoji = "🇧🇦",
            ipAddress = "217.199.128.1",
            pingMs = 46,
            loadPercentage = 22,
            protocol = "ChaCha20 / TLS",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "br-vps-01",
            name = "Brazil",
            country = "Brazil",
            countryCode = "BR",
            flagEmoji = "🇧🇷",
            ipAddress = "177.154.192.45",
            pingMs = 18,
            loadPercentage = 24,
            protocol = "WireGuard UDP",
            isDedicated = false,
            operatorType = OperatorType.BR_VPS
        ),
        VpnServer(
            id = "bg-sofia-01",
            name = "Bulgaria",
            country = "Bulgaria",
            countryCode = "BG",
            flagEmoji = "🇧🇬",
            ipAddress = "194.141.0.1",
            pingMs = 44,
            loadPercentage = 20,
            protocol = "Direct TLS",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "ao-unitel-01",
            name = "Angola (Unitel Sem Saldo)",
            country = "Angola (Unitel)",
            countryCode = "AO",
            flagEmoji = "🇦🇴",
            ipAddress = "197.231.0.12",
            pingMs = 12,
            loadPercentage = 18,
            protocol = "SSL / SNI portal.unitel.ao",
            isDedicated = true,
            operatorType = OperatorType.UNITEL
        ),
        VpnServer(
            id = "ao-unitel-ws",
            name = "Angola (Unitel Websocket)",
            country = "Angola (Unitel)",
            countryCode = "AO",
            flagEmoji = "🇦🇴",
            ipAddress = "197.231.14.88",
            pingMs = 15,
            loadPercentage = 22,
            protocol = "WS CDN / Zero-Rating",
            isDedicated = true,
            operatorType = OperatorType.UNITEL
        ),
        VpnServer(
            id = "ao-africell-01",
            name = "Angola (Africell Congelar)",
            country = "Angola (Africell)",
            countryCode = "AO",
            flagEmoji = "🇦🇴",
            ipAddress = "102.218.12.80",
            pingMs = 8,
            loadPercentage = 15,
            protocol = "Konek SSL / Websocket",
            isDedicated = true,
            operatorType = OperatorType.AFRICELL
        ),
        VpnServer(
            id = "us-ny-01",
            name = "United States",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "104.244.42.1",
            pingMs = 52,
            loadPercentage = 40,
            protocol = "WireGuard / Direct",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "pt-lisbon-01",
            name = "Portugal",
            country = "Portugal",
            countryCode = "PT",
            flagEmoji = "🇵🇹",
            ipAddress = "194.65.0.1",
            pingMs = 36,
            loadPercentage = 28,
            protocol = "TLS / ChaCha20",
            isDedicated = false,
            operatorType = OperatorType.GLOBAL
        ),
        VpnServer(
            id = "fr-vps-01",
            name = "France",
            country = "France",
            countryCode = "FR",
            flagEmoji = "🇫🇷",
            ipAddress = "51.159.88.102",
            pingMs = 45,
            loadPercentage = 31,
            protocol = "ChaCha20-Poly1305 / TLS",
            isDedicated = false,
            operatorType = OperatorType.FR_VPS
        ),
        // REDES SOCIAIS (Facebook, Instagram, Telegram, TikTok, Unitel Social)
        VpnServer(
            id = "soc-all-01",
            name = "Redes Sociais VIP (Multi-App)",
            country = "Redes Sociais",
            countryCode = "GLOBAL",
            flagEmoji = "🌐",
            ipAddress = "104.244.42.1",
            pingMs = 12,
            loadPercentage = 15,
            protocol = "SNI Multi-Social / TLS 1.3",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        ),
        VpnServer(
            id = "soc-fb-01",
            name = "Facebook & Messenger Free",
            country = "Meta Facebook",
            countryCode = "US",
            flagEmoji = "📘",
            ipAddress = "157.240.22.35",
            pingMs = 15,
            loadPercentage = 18,
            protocol = "SSL / SNI m.facebook.com",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        ),
        VpnServer(
            id = "soc-insta-01",
            name = "Instagram Reels & Direct HD",
            country = "Meta Instagram",
            countryCode = "US",
            flagEmoji = "📸",
            ipAddress = "157.240.241.174",
            pingMs = 18,
            loadPercentage = 22,
            protocol = "SSL / SNI instagram.com",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        ),
        VpnServer(
            id = "soc-tg-01",
            name = "Telegram Proxy & Download Rápido",
            country = "Telegram Corp",
            countryCode = "NL",
            flagEmoji = "✈️",
            ipAddress = "149.154.167.99",
            pingMs = 19,
            loadPercentage = 10,
            protocol = "MTProto TLS api.telegram.org",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        ),
        VpnServer(
            id = "soc-tiktok-01",
            name = "TikTok Vídeos & Transmissões",
            country = "ByteDance TikTok",
            countryCode = "SG",
            flagEmoji = "🎵",
            ipAddress = "161.117.156.12",
            pingMs = 21,
            loadPercentage = 16,
            protocol = "SSL / SNI api.tiktokv.com",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        ),
        VpnServer(
            id = "ao-unitel-social",
            name = "Unitel Angola (Pacote Social Grátis)",
            country = "Angola (Unitel)",
            countryCode = "AO",
            flagEmoji = "🇦🇴",
            ipAddress = "197.231.0.18",
            pingMs = 10,
            loadPercentage = 14,
            protocol = "SNI portal.unitel.ao / SSL",
            isDedicated = true,
            operatorType = OperatorType.SOCIAL_NETWORKS
        )
    )

    val fullPayloadList = listOf(
        VpnPayload(
            id = "unitel-zero",
            name = "Unitel - Net Sem Saldo / Sem Prazo 🟠",
            operatorType = OperatorType.UNITEL,
            badgeTitle = "Unitel Zero Data (Sem Saldo)",
            sniHost = "portal.unitel.ao",
            customHeader = "SSL / TLS SNI Direto (portal.unitel.ao) • Sem Saldo / Anti-404",
            description = "Gera internet ilimitada de alta velocidade mesmo sem saldo ou plano ativo na Unitel através do bypass SNI portal.unitel.ao.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "unitel-ws",
            name = "Unitel - Websocket CDN Turbo 🟠",
            operatorType = OperatorType.UNITEL,
            badgeTitle = "Unitel Websocket",
            sniHost = "internetgratis.unitel.ao",
            customHeader = "WebSocket SSL / SNI portal.unitel.ao • Sem Saldo / Anti-404",
            description = "Túnel WebSocket otimizado com injeção HTTP para conexões 3G e 4G da Unitel Angola.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "unitel-dns",
            name = "Unitel - DNS Tunnel (Bypass 53) 🟠",
            operatorType = OperatorType.UNITEL,
            badgeTitle = "Unitel SlowDNS UDP",
            sniHost = "dns.unitel.ao",
            customHeader = "UDP_DNS_PORT_53_TUNNEL",
            description = "Tunelamento por consultas DNS porta 53 que ignora restrições de firewall Unitel com saldo zero.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        // REDES SOCIAIS PAYLOADS
        VpnPayload(
            id = "payload-social-all",
            name = "Redes Sociais - Multi-App Turbo 🌐",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "Redes Sociais VIP",
            sniHost = "graph.facebook.com",
            customHeader = "SSL / TLS SNI Multi-App (Facebook, Instagram, Telegram, TikTok)",
            description = "Acesso livre a Facebook, Instagram, Telegram e TikTok com baixa latência e velocidade ilimitada.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "payload-fb-free",
            name = "Facebook & Messenger Ilimitado 📘",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "Facebook Free SNI",
            sniHost = "m.facebook.com",
            customHeader = "SSL / TLS SNI m.facebook.com (Meta Free Zero)",
            description = "Bypass de dados para navegar no Facebook e Messenger sem gastar pacote de internet.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "payload-insta-reels",
            name = "Instagram Reels & Vídeos HD 📸",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "Instagram Direct",
            sniHost = "instagram.com",
            customHeader = "SSL / TLS SNI instagram.com / CDN Direct",
            description = "Otimizado para carregar Reels, fotos e stories sem travar com compressão inteligente.",
            isFreezeData = false,
            isZeroBalance = false
        ),
        VpnPayload(
            id = "payload-tg-mtproto",
            name = "Telegram Anti-Bloqueio & Download ✈️",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "Telegram Proxy",
            sniHost = "api.telegram.org",
            customHeader = "SSL / MTProto TLS api.telegram.org",
            description = "Túnel direto para chamadas de voz, envio de vídeos e download super veloz no Telegram.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "payload-tiktok-stream",
            name = "TikTok Vídeos Ilimitados 🎵",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "TikTok Stream",
            sniHost = "api.tiktokv.com",
            customHeader = "SSL / TLS SNI api.tiktokv.com / ByteDance CDN",
            description = "Streaming de alta taxa de quadros e lives fluidas sem interrupções.",
            isFreezeData = false,
            isZeroBalance = false
        ),
        VpnPayload(
            id = "payload-unitel-social",
            name = "Unitel - Converter Pacote Redes Sociais 🇦🇴",
            operatorType = OperatorType.SOCIAL_NETWORKS,
            badgeTitle = "Unitel Pacote Social",
            sniHost = "portal.unitel.ao",
            customHeader = "SSL / TLS SNI portal.unitel.ao / Redes Sociais VIP",
            description = "Converte o pacote de redes sociais da Unitel em internet livre total para navegar em qualquer site e app.",
            isFreezeData = false,
            isZeroBalance = true
        ),
        VpnPayload(
            id = "africell-freezer",
            name = "Africell - Internet Congelar Saldo 🟣",
            operatorType = OperatorType.AFRICELL,
            badgeTitle = "Africell Data Freezer",
            sniHost = "freezer.africell.ao",
            customHeader = "SSL / TLS SNI freezer.africell.ao",
            description = "Congela o consumo do seu pacote Africell. Conectividade direta de alta velocidade.",
            isFreezeData = true,
            isZeroBalance = false
        ),
        VpnPayload(
            id = "br-direct",
            name = "Brasil - High Speed Direct 🇧🇷",
            operatorType = OperatorType.BR_VPS,
            badgeTitle = "VPS Brasil",
            sniHost = "br.konek-vpn.net",
            customHeader = "DIRECT_UDP",
            description = "Otimizado para máxima velocidade de download, jogos sem lag e transmissões HD.",
            isFreezeData = false,
            isZeroBalance = false
        ),
        VpnPayload(
            id = "fr-direct",
            name = "França - High Speed Direct 🇫🇷",
            operatorType = OperatorType.FR_VPS,
            badgeTitle = "VPS França",
            sniHost = "fr.konek-vpn.net",
            customHeader = "DIRECT_TLS",
            description = "Criptografia forte europeia de ponta a ponta com privacidade absoluta.",
            isFreezeData = false,
            isZeroBalance = false
        )
    )

    fun applyUpdate() {
        _isListUpdated.value = true
        _availableServers.value = fullServerList
        _availablePayloads.value = fullPayloadList
        if (_selectedServer.value == null) {
            _selectedServer.value = fullServerList[0]
        }
        if (_selectedPayload.value == null) {
            _selectedPayload.value = fullPayloadList[0]
        }
    }

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun selectPayload(payload: VpnPayload) {
        _selectedPayload.value = payload
    }

    fun measureServerPing(serverId: String): Int {
        return Random.nextInt(12, 55)
    }
}
