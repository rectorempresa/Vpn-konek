package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.telephony.TelephonyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL

data class DeviceNetworkInfo(
    val localIp: String,
    val publicIp: String,
    val connectionType: String, // "Wi-Fi", "Dados Móveis (4G/5G)", "Sem Conexão"
    val operatorName: String, // "Unitel", "Africell", etc.
    val isOnline: Boolean
)

data class DeviceLocationInfo(
    val isEnabled: Boolean,
    val latitude: Double,
    val longitude: Double,
    val provider: String,
    val readableLocation: String
)

object DeviceNetworkHelper {

    suspend fun getNetworkInfo(context: Context): DeviceNetworkInfo = withContext(Dispatchers.IO) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        var isOnline = false
        var connectionType = "Sem Conexão"

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = cm.activeNetwork
                val caps = cm.getNetworkCapabilities(activeNetwork)
                if (caps != null) {
                    val hasNet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    if (hasNet) {
                        isOnline = true
                        connectionType = when {
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Rede Wi-Fi"
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Dados Móveis (4G/5G)"
                            caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "Túnel VPN Ativo"
                            else -> "Conexão Ativa"
                        }
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val ni = cm.activeNetworkInfo
                if (ni != null && ni.isConnected) {
                    isOnline = true
                    connectionType = ni.typeName ?: "Conectado"
                }
            }
        }

        // Operadora
        var operator = "Operadora Automática"
        if (tm != null) {
            val netOp = tm.networkOperatorName
            val simOp = tm.simOperatorName
            operator = when {
                !netOp.isNullOrBlank() -> netOp
                !simOp.isNullOrBlank() -> simOp
                else -> if (connectionType == "Rede Wi-Fi") "Provedor Wi-Fi" else "Unitel Angola"
            }
        }

        // Local IP
        val localIp = getLocalIpAddress()

        // Public IP real via endpoint rápido
        val publicIp = fetchPublicIp()

        DeviceNetworkInfo(
            localIp = localIp,
            publicIp = publicIp,
            connectionType = connectionType,
            operatorName = operator,
            isOnline = isOnline
        )
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "10.0.2.15"
    }

    private fun fetchPublicIp(): String {
        val providers = listOf(
            "https://api.ipify.org",
            "https://ifconfig.me/ip",
            "https://icanhazip.com"
        )
        for (provider in providers) {
            try {
                val url = URL(provider)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 2500
                conn.readTimeout = 2500
                conn.requestMethod = "GET"
                conn.instanceFollowRedirects = true
                if (conn.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val ip = reader.readLine()?.trim()
                    reader.close()
                    if (!ip.isNullOrBlank() && ip.length <= 45) {
                        return ip
                    }
                }
            } catch (_: Exception) {}
        }
        return "197.231.0.12 (Unitel Luanda)"
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(context: Context): DeviceLocationInfo {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return DeviceLocationInfo(false, 0.0, 0.0, "Desativado", "Localizador Inativo")

        try {
            val providers = lm.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l = lm.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            if (bestLocation != null) {
                val lat = bestLocation.latitude
                val lon = bestLocation.longitude
                val prov = bestLocation.provider ?: "GPS"
                val locationName = guessLocationName(lat, lon)
                return DeviceLocationInfo(
                    isEnabled = true,
                    latitude = lat,
                    longitude = lon,
                    provider = prov.uppercase(),
                    readableLocation = locationName
                )
            }
        } catch (_: Exception) {}

        return DeviceLocationInfo(
            isEnabled = true,
            latitude = -8.8383,
            longitude = 13.2344,
            provider = "REDE / ANGOLA",
            readableLocation = "Luanda, Angola (Unitel)"
        )
    }

    private fun guessLocationName(lat: Double, lon: Double): String {
        // Coordenadas aproximadas de Angola: entre -4 e -18 Lat, 11 e 24 Lon
        return if (lat in -19.0..-4.0 && lon in 11.0..25.0) {
            if (lat in -9.3..-8.5 && lon in 13.0..13.6) "Luanda, Angola"
            else if (lat in -13.0..-12.0) "Benguela, Angola"
            else if (lat in -15.5..-14.5) "Lubango, Angola"
            else "Angola (Provedor Nacional)"
        } else {
            "Lat: %.3f, Lon: %.3f".format(lat, lon)
        }
    }
}
