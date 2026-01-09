package com.example.seedlife.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.repository.SeedRepository
import com.example.seedlife.ui.common.UiState
import com.example.seedlife.util.FirebaseErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para HomeScreen
 */
class HomeViewModel(
    private val uid: String,
    private val isGuest: Boolean,
    private val seedRepository: SeedRepository = SeedRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Seed>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Seed>>> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Para modo invitado: seeds en memoria
    private val guestSeeds = mutableListOf<Seed>()

    init {
        if (isGuest) {
            // Inicializar con seeds de ejemplo para invitado
            guestSeeds.addAll(
                listOf(
                    Seed(id = "seed1", title = "Semilla de Ejemplo 1", description = "Una semilla de ejemplo", level = 1),
                    Seed(id = "seed2", title = "Semilla de Ejemplo 2", description = "Otra semilla de ejemplo", level = 2)
                )
            )
            _uiState.value = UiState.Success(guestSeeds.toList())
        } else {
            // Observar seeds desde Firestore
            viewModelScope.launch {
                try {
                    seedRepository.observeSeeds(uid).collect { seeds ->
                        _uiState.value = UiState.Success(seeds)
                    }
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retry() }
                    )
                }
            }
        }
    }

    /**
     * Reintenta cargar las seeds
     */
    fun retry() {
        if (!isGuest) {
            _uiState.value = UiState.Loading
            viewModelScope.launch {
                try {
                    seedRepository.observeSeeds(uid).collect { seeds ->
                        _uiState.value = UiState.Success(seeds)
                    }
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retry() }
                    )
                }
            }
        }
    }

    /**
     * Elimina una seed (para invitado: en memoria, para usuario: en Firestore)
     */
    fun deleteSeed(seedId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (isGuest) {
                // Eliminar de memoria
                guestSeeds.removeAll { it.id == seedId }
                _uiState.value = UiState.Success(guestSeeds.toList())
                _snackbarMessage.value = "Semilla eliminada"
                onSuccess()
            } else {
                val result = seedRepository.deleteSeed(uid, seedId)
                result.fold(
                    onSuccess = {
                        _snackbarMessage.value = "Semilla eliminada"
                        onSuccess()
                    },
                    onFailure = { throwable ->
                        _snackbarMessage.value = FirebaseErrorMapper.mapException(
                            throwable as? Exception ?: Exception(throwable.message, throwable)
                        )
                    }
                )
            }
        }
    }

    /**
     * Crea una seed (para invitado: en memoria, para usuario: en Firestore)
     */
    fun createSeed(title: String, description: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            if (isGuest) {
                val newSeed = Seed(
                    id = "guest_seed_${System.currentTimeMillis()}",
                    title = title,
                    description = description,
                    level = 1
                )
                guestSeeds.add(newSeed)
                _uiState.value = UiState.Success(guestSeeds.toList())
                _snackbarMessage.value = "Semilla creada"
                onSuccess(newSeed.id)
            } else {
                val result = seedRepository.createSeed(uid, title, description)
                result.fold(
                    onSuccess = { seedId ->
                        _snackbarMessage.value = "Semilla creada"
                        onSuccess(seedId)
                    },
                    onFailure = { throwable ->
                        _snackbarMessage.value = FirebaseErrorMapper.mapException(
                            throwable as? Exception ?: Exception(throwable.message, throwable)
                        )
                    }
                )
            }
        }
    }

    /**
     * Actualiza una seed (para invitado: en memoria, para usuario: en Firestore)
     */
    fun updateSeed(seedId: String, title: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (isGuest) {
                val index = guestSeeds.indexOfFirst { it.id == seedId }
                if (index >= 0) {
                    guestSeeds[index] = guestSeeds[index].copy(
                        title = title,
                        description = description
                    )
                    _uiState.value = UiState.Success(guestSeeds.toList())
                }
                _snackbarMessage.value = "Semilla actualizada"
                onSuccess()
            } else {
                val result = seedRepository.updateSeed(uid, seedId, title, description)
                result.fold(
                    onSuccess = {
                        _snackbarMessage.value = "Semilla actualizada"
                        onSuccess()
                    },
                    onFailure = { throwable ->
                        _snackbarMessage.value = FirebaseErrorMapper.mapException(
                            throwable as? Exception ?: Exception(throwable.message, throwable)
                        )
                    }
                )
            }
        }
    }

    /**
     * Limpia el mensaje del snackbar
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

/**
 * Factory para HomeViewModel
 */
class HomeViewModelFactory(
    private val uid: String,
    private val isGuest: Boolean
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(uid, isGuest) as T
    }
}
