package com.example.seedlife.ui.seeddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.example.seedlife.data.repository.SeedRepository
import com.example.seedlife.ui.common.UiState
import com.example.seedlife.util.FirebaseErrorMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para el detalle de una seed
 */
class SeedDetailViewModel(
    private val seedRepository: SeedRepository = SeedRepository(),
    private val uid: String,
    private val seedId: String,
    private val isGuest: Boolean = false
) : ViewModel() {

    private val _seed = MutableStateFlow<UiState<Seed?>>(UiState.Loading)
    val seed: StateFlow<UiState<Seed?>> = _seed.asStateFlow()

    private val _waterings = MutableStateFlow<UiState<List<Watering>>>(UiState.Loading)
    val waterings: StateFlow<UiState<List<Watering>>> = _waterings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Para modo invitado: datos en memoria
    private var guestSeed: Seed? = null
    private val guestWaterings = mutableListOf<Watering>()

    init {
        if (isGuest) {
            // Inicializar seed de invitado (placeholder)
            guestSeed = Seed(
                id = seedId,
                title = "Semilla de Ejemplo",
                description = "Esta es una semilla de ejemplo en modo invitado",
                level = 1
            )
            _seed.value = UiState.Success(guestSeed)
            _waterings.value = UiState.Success(emptyList())
        } else {
            // Observar seed en tiempo real desde Firestore
            viewModelScope.launch {
                try {
                    seedRepository.observeSeed(uid, seedId).collect { seed ->
                        _seed.value = UiState.Success(seed)
                    }
                } catch (e: Exception) {
                    _seed.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retrySeed() }
                    )
                }
            }

            // Observar waterings en tiempo real desde Firestore
            viewModelScope.launch {
                try {
                    seedRepository.observeWaterings(uid, seedId).collect { waterings ->
                        _waterings.value = UiState.Success(waterings)
                    }
                } catch (e: Exception) {
                    _waterings.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retryWaterings() }
                    )
                }
            }
        }
    }

    /**
     * Reintenta cargar la seed
     */
    fun retrySeed() {
        if (!isGuest) {
            _seed.value = UiState.Loading
            viewModelScope.launch {
                try {
                    seedRepository.observeSeed(uid, seedId).collect { seed ->
                        _seed.value = UiState.Success(seed)
                    }
                } catch (e: Exception) {
                    _seed.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retrySeed() }
                    )
                }
            }
        }
    }

    /**
     * Reintenta cargar los waterings
     */
    fun retryWaterings() {
        if (!isGuest) {
            _waterings.value = UiState.Loading
            viewModelScope.launch {
                try {
                    seedRepository.observeWaterings(uid, seedId).collect { waterings ->
                        _waterings.value = UiState.Success(waterings)
                    }
                } catch (e: Exception) {
                    _waterings.value = UiState.Error(
                        message = FirebaseErrorMapper.mapException(e),
                        retry = { retryWaterings() }
                    )
                }
            }
        }
    }

    /**
     * Añade un nuevo riego
     */
    fun addWatering(mood: WateringMood, note: String?) {
        if (isGuest) {
            // Modo invitado: añadir en memoria
            val newWatering = Watering(
                id = System.currentTimeMillis().toString(),
                mood = mood.name,
                note = note,
                date = java.util.Date(),
                createdAt = java.util.Date()
            )
            guestWaterings.add(0, newWatering) // Añadir al inicio
            _waterings.value = guestWaterings.toList()

            // Actualizar level en memoria
            val totalWaterings = guestWaterings.size
            val newLevel = minOf(5, 1 + (totalWaterings / 3))
            guestSeed = guestSeed?.copy(
                level = newLevel,
                lastWateredAt = java.util.Date()
            )
            _seed.value = guestSeed
        } else {
            // Modo normal: añadir a Firestore
            viewModelScope.launch {
                _isLoading.value = true

                val result = seedRepository.addWatering(uid, seedId, mood, note)
                result.fold(
                    onSuccess = {
                        _isLoading.value = false
                        _snackbarMessage.value = "Riego guardado"
                        // El level se actualizará automáticamente por observeSeed
                    },
                    onFailure = { exception ->
                        _isLoading.value = false
                        _snackbarMessage.value = FirebaseErrorMapper.mapException(exception)
                    }
                )
            }
        }
    }

    /**
     * Elimina la seed actual
     */
    fun deleteSeed(onSuccess: () -> Unit) {
        if (isGuest) {
            // Para invitado, se maneja desde fuera
            onSuccess()
        } else {
            viewModelScope.launch {
                _isLoading.value = true
                val result = seedRepository.deleteSeed(uid, seedId)
                result.fold(
                    onSuccess = {
                        _isLoading.value = false
                        _snackbarMessage.value = "Semilla eliminada"
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _isLoading.value = false
                        _snackbarMessage.value = FirebaseErrorMapper.mapException(exception)
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
