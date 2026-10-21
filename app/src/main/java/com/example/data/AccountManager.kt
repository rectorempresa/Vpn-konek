package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.model.UserAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object AccountManager {
    private const val TAG = "AccountManager"
    private const val PREFS_NAME = "konek_accounts_storage"
    private const val KEY_ACCOUNTS_JSON = "accounts_list_json"
    private const val KEY_ACTIVE_USER_ID = "active_user_id"
    private const val KEY_GUEST_REMAINING_TIME = "guest_remaining_time"

    private lateinit var prefs: SharedPreferences

    private val _accounts = MutableStateFlow<List<UserAccount>>(emptyList())
    val accounts: StateFlow<List<UserAccount>> = _accounts.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    // Default guest account if not logged in
    val guestAccount = UserAccount(
        id = "guest_device",
        username = "Visitante",
        email = "visitante@konekvpn.local",
        phone = "",
        password = "",
        remainingTimeSeconds = 300L, // 5 min de teste
        isVip = false
    )

    fun initialize(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadAccountsFromStorage()
    }

    private fun loadAccountsFromStorage() {
        val jsonStr = prefs.getString(KEY_ACCOUNTS_JSON, null)
        val loadedList = mutableListOf<UserAccount>()

        if (!jsonStr.isNullOrBlank()) {
            try {
                val jsonArray = JSONArray(jsonStr)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val couponsArray = obj.optJSONArray("usedCoupons")
                    val coupons = mutableListOf<String>()
                    if (couponsArray != null) {
                        for (c in 0 until couponsArray.length()) {
                            coupons.add(couponsArray.getString(c))
                        }
                    }
                    loadedList.add(
                        UserAccount(
                            id = obj.getString("id"),
                            username = obj.getString("username"),
                            email = obj.getString("email"),
                            phone = obj.optString("phone", ""),
                            password = obj.getString("password"),
                            remainingTimeSeconds = obj.optLong("remainingTimeSeconds", 300L),
                            isVip = obj.optBoolean("isVip", false),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                            usedCoupons = coupons
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao decodificar contas salvas: ${e.message}")
            }
        }

        // Se a lista estiver vazia, adicionamos contas iniciais: a de Admin e uma conta Demo
        if (loadedList.isEmpty()) {
            val adminAcc = UserAccount(
                id = "admin_isaac",
                username = "isaacmuaco11",
                email = "isaacmuaco11@konekvpn.com",
                phone = "+244 923 000 000",
                password = "14136920",
                remainingTimeSeconds = 86400L * 365, // 1 ano para o Admin Isaac Muaco
                isVip = true
            )
            val demoUser = UserAccount(
                id = "user_angola_01",
                username = "cliente_angola",
                email = "cliente@unitel.ao",
                phone = "+244 945 123 456",
                password = "123456",
                remainingTimeSeconds = 7200L, // 2 Horas
                isVip = false
            )
            loadedList.add(adminAcc)
            loadedList.add(demoUser)
        }

        _accounts.value = loadedList
        saveAccountsToStorage(loadedList)

        // Carregar sessão ativa
        val activeId = prefs.getString(KEY_ACTIVE_USER_ID, null)
        val matchedUser = loadedList.find { it.id == activeId }
        if (matchedUser != null) {
            _currentUser.value = matchedUser
        } else {
            // Carregar tempo do guest salvo ou default
            val guestTime = prefs.getLong(KEY_GUEST_REMAINING_TIME, 300L)
            guestAccount.remainingTimeSeconds = guestTime
            _currentUser.value = guestAccount
        }
    }

    private fun saveAccountsToStorage(accountsList: List<UserAccount>) {
        try {
            val jsonArray = JSONArray()
            for (acc in accountsList) {
                val obj = JSONObject()
                obj.put("id", acc.id)
                obj.put("username", acc.username)
                obj.put("email", acc.email)
                obj.put("phone", acc.phone)
                obj.put("password", acc.password)
                obj.put("remainingTimeSeconds", acc.remainingTimeSeconds)
                obj.put("isVip", acc.isVip)
                obj.put("createdAt", acc.createdAt)
                val cArray = JSONArray()
                acc.usedCoupons.forEach { cArray.put(it) }
                obj.put("usedCoupons", cArray)
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_ACCOUNTS_JSON, jsonArray.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao salvar contas no disco: ${e.message}")
        }
    }

    fun registerAccount(
        username: String,
        email: String,
        phone: String,
        pass: String
    ): Pair<Boolean, String> {
        val trimmedUser = username.trim()
        val trimmedEmail = email.trim().lowercase()
        val trimmedPhone = phone.trim()
        val trimmedPass = pass.trim()

        if (trimmedUser.length < 3) return Pair(false, "Nome de usuário deve ter pelo menos 3 caracteres.")
        if (!trimmedEmail.contains("@")) return Pair(false, "Digite um e-mail válido.")
        if (trimmedPass.length < 4) return Pair(false, "A senha deve ter pelo menos 4 dígitos.")

        val currentList = _accounts.value.toMutableList()
        if (currentList.any { it.username.equals(trimmedUser, ignoreCase = true) }) {
            return Pair(false, "Já existe uma conta com este nome de usuário!")
        }
        if (currentList.any { it.email.equals(trimmedEmail, ignoreCase = true) }) {
            return Pair(false, "Este e-mail já foi cadastrado!")
        }

        val newAcc = UserAccount(
            id = "user_${System.currentTimeMillis()}",
            username = trimmedUser,
            email = trimmedEmail,
            phone = trimmedPhone,
            password = trimmedPass,
            remainingTimeSeconds = 900L, // 15 Minutos de Bônus de Cadastro Inicial
            isVip = false
        )

        currentList.add(0, newAcc)
        _accounts.value = currentList
        saveAccountsToStorage(currentList)

        // Loga automaticamente
        loginUser(trimmedUser, trimmedPass)
        return Pair(true, "Conta criada com sucesso! Bônus de 15 minutos adicionado.")
    }

    fun loginUser(userOrEmail: String, pass: String): Pair<Boolean, String> {
        val cleanInput = userOrEmail.trim().lowercase()
        val cleanPass = pass.trim()

        val found = _accounts.value.find {
            (it.username.lowercase() == cleanInput || it.email.lowercase() == cleanInput) &&
                    it.password == cleanPass
        }

        return if (found != null) {
            _currentUser.value = found
            prefs.edit().putString(KEY_ACTIVE_USER_ID, found.id).apply()
            Pair(true, "Bem-vindo de volta, ${found.username}!")
        } else {
            Pair(false, "Usuário/E-mail ou senha incorretos.")
        }
    }

    fun logout() {
        // Salvar estado da conta antes de sair
        persistCurrentUserTime(_currentUser.value?.remainingTimeSeconds ?: 300L)
        prefs.edit().remove(KEY_ACTIVE_USER_ID).apply()
        _currentUser.value = guestAccount
    }

    fun persistCurrentUserTime(newTimeSeconds: Long) {
        val user = _currentUser.value ?: return
        user.remainingTimeSeconds = newTimeSeconds

        if (user.id == guestAccount.id) {
            prefs.edit().putLong(KEY_GUEST_REMAINING_TIME, newTimeSeconds).apply()
        } else {
            val list = _accounts.value.toMutableList()
            val idx = list.indexOfFirst { it.id == user.id }
            if (idx != -1) {
                list[idx] = user
                _accounts.value = list
                saveAccountsToStorage(list)
            }
        }
    }

    fun addTimeToActiveUser(seconds: Long) {
        val user = _currentUser.value ?: return
        val updated = user.remainingTimeSeconds + seconds
        persistCurrentUserTime(updated)
    }

    fun recordCouponUsed(couponCode: String) {
        val user = _currentUser.value ?: return
        if (!user.usedCoupons.contains(couponCode)) {
            user.usedCoupons.add(couponCode)
            persistCurrentUserTime(user.remainingTimeSeconds)
        }
    }
}
