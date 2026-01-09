package com.example.seedlife.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seedlife.data.model.User
import com.example.seedlife.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la autenticación
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String, val user: User?, val isGuest: Boolean = false) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel para manejar la autenticación
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    init {
        // Verificar si hay un usuario autenticado al iniciar
        checkCurrentUser()
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    private fun checkCurrentUser() {
        if (authRepository.isUserLoggedIn()) {
            val uid = authRepository.getCurrentUserId()
            if (uid != null) {
                loadUserData(uid)
            }
        }
    }

    /**
     * Registra un nuevo usuario
     */
    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.register(email, password, name)) {
                is Result.Success -> {
                    loadUserData(result.getOrNull() ?: return@launch)
                }
                is Result.Failure -> {
                    _authState.value = AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Error desconocido al registrar"
                    )
                }
            }
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    loadUserData(result.getOrNull() ?: return@launch)
                }
                is Result.Failure -> {
                    _authState.value = AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Error desconocido al iniciar sesión"
                    )
                }
            }
        }
    }

    /**
     * Entra como invitado (sin autenticación Firebase)
     */
    fun enterAsGuest() {
        _authState.value = AuthState.Success(
            userId = "guest",
            user = null,
            isGuest = true
        )
    }

    /**
     * Carga los datos del usuario desde Firestore
     */
    private fun loadUserData(uid: String) {
        viewModelScope.launch {
            when (val result = authRepository.getUserData(uid)) {
                is Result.Success -> {
                    val user = result.getOrNull()
                    _userData.value = user
                    _authState.value = AuthState.Success(
                        userId = uid,
                        user = user,
                        isGuest = false
                    )
                }
                is Result.Failure -> {
                    _authState.value = AuthState.Error(
                        result.exceptionOrNull()?.message ?: "Error al cargar datos del usuario"
                    )
                }
            }
        }
    }

    /**
     * Cierra sesión
     */
    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
        _userData.value = null
    }

    /**
     * Resetea el estado de error
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
