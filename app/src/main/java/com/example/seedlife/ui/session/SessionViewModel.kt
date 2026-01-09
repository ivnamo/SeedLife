package com.example.seedlife.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seedlife.data.model.UserProfile
import com.example.seedlife.data.repository.AuthRepository
import com.example.seedlife.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado de la sesión
 */
data class SessionState(
    val uid: String? = null,
    val isGuest: Boolean = false,
    val userProfile: UserProfile? = null
)

/**
 * ViewModel para manejar el estado global de la sesión
 */
class SessionViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _sessionState = MutableStateFlow<SessionState>(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    init {
        // Verificar si hay un usuario autenticado al iniciar
        checkCurrentSession()
    }

    /**
     * Verifica la sesión actual
     */
    private fun checkCurrentSession() {
        if (authRepository.isUserLoggedIn()) {
            val uid = authRepository.getCurrentUserId()
            if (uid != null) {
                setAuthenticatedUser(uid)
            }
        }
    }

    /**
     * Establece un usuario autenticado
     */
    fun setAuthenticatedUser(uid: String) {
        _sessionState.value = _sessionState.value.copy(
            uid = uid,
            isGuest = false
        )
        
        // Observar perfil del usuario
        viewModelScope.launch {
            userRepository.observeUserProfile(uid).collect { profile ->
                _sessionState.value = _sessionState.value.copy(
                    userProfile = profile
                )
            }
        }
    }

    /**
     * Establece modo invitado
     */
    fun setGuestMode() {
        _sessionState.value = SessionState(
            uid = null,
            isGuest = true,
            userProfile = null
        )
    }

    /**
     * Cierra sesión
     */
    fun signOut() {
        userRepository.signOut()
        _sessionState.value = SessionState()
    }
}
