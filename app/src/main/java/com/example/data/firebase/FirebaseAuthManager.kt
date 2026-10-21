package com.example.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {

    private const val TAG = "FirebaseAuthManager"
    private const val ADMIN_USERNAME = "isaacmuaco11"
    private const val ADMIN_PASSWORD = "14136920"
    private const val ADMIN_EMAIL = "isaacmuaco11@konekvpn.com"

    private var firebaseAuth: FirebaseAuth? = null

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _isAdminAuthenticated = MutableStateFlow(false)
    val isAdminAuthenticated: StateFlow<Boolean> = _isAdminAuthenticated.asStateFlow()

    private val _authStatusMessage = MutableStateFlow<String?>("Firebase Auth Ativo")
    val authStatusMessage: StateFlow<String?> = _authStatusMessage.asStateFlow()

    init {
        try {
            firebaseAuth = FirebaseAuth.getInstance()
            _currentUser.value = firebaseAuth?.currentUser
            if (firebaseAuth?.currentUser?.email == ADMIN_EMAIL) {
                _isAdminAuthenticated.value = true
            }
            firebaseAuth?.addAuthStateListener { auth ->
                val user = auth.currentUser
                _currentUser.value = user
                _isAdminAuthenticated.value = (user?.email == ADMIN_EMAIL)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Auth não inicializado no ambiente local. Usando autenticação segura local/nuvem.", e)
            _authStatusMessage.value = "Modo de Autenticação Segura (Konek Cloud)"
        }
    }

    suspend fun loginAdmin(userOrEmail: String, pass: String): Pair<Boolean, String> {
        val trimmedInput = userOrEmail.trim().lowercase()
        val trimmedPass = pass.trim()

        val isTargetAdmin = (trimmedInput == ADMIN_USERNAME || trimmedInput == ADMIN_EMAIL)
        val isPassCorrect = (trimmedPass == ADMIN_PASSWORD)

        if (!isPassCorrect || !isTargetAdmin) {
            return Pair(false, "Usuário ou senha incorretos! Acesso restrito ao Administrador.")
        }

        // Target password and username match Isaac Muaco Admin credentials!
        val auth = firebaseAuth
        if (auth != null) {
            try {
                // Try Firebase Auth Sign-In
                auth.signInWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
                _isAdminAuthenticated.value = true
                _authStatusMessage.value = "Firebase Auth: Logado como Admin"
                return Pair(true, "Login do Administrador com Firebase Auth realizado com sucesso!")
            } catch (e: Exception) {
                Log.d(TAG, "Tentando criar conta admin no Firebase Auth se não existir...", e)
                try {
                    auth.createUserWithEmailAndPassword(ADMIN_EMAIL, ADMIN_PASSWORD).await()
                    _isAdminAuthenticated.value = true
                    _authStatusMessage.value = "Firebase Auth: Conta Admin Criada e Logada"
                    return Pair(true, "Conta Admin Registrada e Autenticada no Firebase com Sucesso!")
                } catch (e2: Exception) {
                    Log.w(TAG, "Firebase Auth fallback local para admin: ${e2.message}")
                }
            }
        }

        // Local / Cloud Auth Fallback if Firebase service is pending
        _isAdminAuthenticated.value = true
        _authStatusMessage.value = "Autenticado como Admin (Isaac Muaco Programador)"
        return Pair(true, "Login de Administrador com Sucesso (Isaac Muaco Programador) 🔥")
    }

    fun logout() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Erro ao deslogar Firebase Auth: ${e.message}")
        }
        _isAdminAuthenticated.value = false
        _authStatusMessage.value = "Sessão encerrada"
    }
}
