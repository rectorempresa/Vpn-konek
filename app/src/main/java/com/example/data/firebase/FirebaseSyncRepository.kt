package com.example.data.firebase

import android.util.Log
import com.example.data.VpnRepository
import com.example.model.VoucherCode
import com.example.model.VpnPayload
import com.example.model.VpnServer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object FirebaseSyncRepository {

    private const val TAG = "FirebaseSyncRepository"
    private const val COLLECTION_KONEK = "konek_vpn_data"
    private const val DOC_SERVERS = "servers_and_payloads"
    private const val DOC_VOUCHERS = "vouchers_cloud"
    private const val DOC_TIMER = "timer_cloud"

    private var firestore: FirebaseFirestore? = null

    private val _isCloudSynced = MutableStateFlow(false)
    val isCloudSynced: StateFlow<Boolean> = _isCloudSynced.asStateFlow()

    private val _lastCloudSyncTimeMessage = MutableStateFlow("Sincronizado com Nuvem Firebase")
    val lastCloudSyncTimeMessage: StateFlow<String> = _lastCloudSyncTimeMessage.asStateFlow()

    init {
        try {
            firestore = FirebaseFirestore.getInstance()
            _isCloudSynced.value = true
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore não inicializado no ambiente local.", e)
            _isCloudSynced.value = false
            _lastCloudSyncTimeMessage.value = "Modo Local & Nuvem Konek Ativo"
        }
    }

    suspend fun syncServersAndPayloadsToFirebase(): Boolean {
        val db = firestore ?: return false
        return try {
            val serverListMap = VpnRepository.fullServerList.map { server ->
                mapOf(
                    "id" to server.id,
                    "name" to server.name,
                    "country" to server.country,
                    "countryCode" to server.countryCode,
                    "flagEmoji" to server.flagEmoji,
                    "ipAddress" to server.ipAddress,
                    "pingMs" to server.pingMs,
                    "protocol" to server.protocol,
                    "isDedicated" to server.isDedicated
                )
            }

            val payloadListMap = VpnRepository.fullPayloadList.map { payload ->
                mapOf(
                    "id" to payload.id,
                    "name" to payload.name,
                    "badgeTitle" to payload.badgeTitle,
                    "sniHost" to payload.sniHost,
                    "description" to payload.description,
                    "isFreezeData" to payload.isFreezeData,
                    "isZeroBalance" to payload.isZeroBalance
                )
            }

            val data = mapOf(
                "isUpdated" to true,
                "updatedAt" to System.currentTimeMillis(),
                "servers" to serverListMap,
                "payloads" to payloadListMap,
                "author" to "Isaac Muaco Programador"
            )

            db.collection(COLLECTION_KONEK).document(DOC_SERVERS).set(data, SetOptions.merge()).await()
            _isCloudSynced.value = true
            _lastCloudSyncTimeMessage.value = "Servidores atualizados na Nuvem Firebase! 🔥"
            true
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao salvar servidores no Firebase: ${e.message}")
            _lastCloudSyncTimeMessage.value = "Lista Atualizada (Konek Local & Remote)"
            false
        }
    }

    suspend fun saveVouchersToFirebase(vouchers: List<VoucherCode>): Boolean {
        val db = firestore ?: return false
        return try {
            val vouchersMap = vouchers.map { v ->
                mapOf(
                    "code" to v.code,
                    "durationMinutes" to v.durationMinutes,
                    "timeLabel" to v.timeLabel,
                    "isUsed" to v.isUsed,
                    "createdAt" to v.createdAt
                )
            }

            val data = mapOf(
                "vouchers" to vouchersMap,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(COLLECTION_KONEK).document(DOC_VOUCHERS).set(data, SetOptions.merge()).await()
            _isCloudSynced.value = true
            true
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao salvar cupons no Firebase: ${e.message}")
            false
        }
    }

    suspend fun saveRemainingTimeToFirebase(seconds: Long): Boolean {
        val db = firestore ?: return false
        return try {
            val data = mapOf(
                "remainingTimeSeconds" to seconds,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(COLLECTION_KONEK).document(DOC_TIMER).set(data, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao salvar timer no Firebase: ${e.message}")
            false
        }
    }
}
