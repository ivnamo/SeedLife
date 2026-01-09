package com.example.seedlife.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.repository.SeedRepository
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

    private val _seeds = MutableStateFlow<List<Seed>>(emptyList())
    val seeds: StateFlow<List<Seed>> = _seeds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
            _seeds.value = guestSeeds.toList()
        } else {
            // Observar seeds desde Firestore
            viewModelScope.launch {
                seedRepository.observeSeeds(uid).collect { seeds ->
                    _seeds.value = seeds
                }
            }
        }
    }

    /**
     * Elimina una seed (para invitado: en memoria, para usuario: en Firestore)
     */
    fun deleteSeed(seedId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            if (isGuest) {
                // Eliminar de memoria
                guestSeeds.removeAll { it.id == seedId }
                _seeds.value = guestSeeds.toList()
                _isLoading.value = false
                onSuccess()
            } else {
                val result = seedRepository.deleteSeed(uid, seedId)
                result.fold(
                    onSuccess = {
                        _isLoading.value = false
                        onSuccess()
                    },
                    onFailure = {
                        _isLoading.value = false
                        // Error se maneja desde fuera
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
            _isLoading.value = true
            if (isGuest) {
                val newSeed = Seed(
                    id = "guest_seed_${System.currentTimeMillis()}",
                    title = title,
                    description = description,
                    level = 1
                )
                guestSeeds.add(newSeed)
                _seeds.value = guestSeeds.toList()
                _isLoading.value = false
                onSuccess(newSeed.id)
            } else {
                val result = seedRepository.createSeed(uid, title, description)
                result.fold(
                    onSuccess = { seedId ->
                        _isLoading.value = false
                        onSuccess(seedId)
                    },
                    onFailure = {
                        _isLoading.value = false
                        // Error se maneja desde fuera
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
            _isLoading.value = true
            if (isGuest) {
                val index = guestSeeds.indexOfFirst { it.id == seedId }
                if (index >= 0) {
                    guestSeeds[index] = guestSeeds[index].copy(
                        title = title,
                        description = description
                    )
                    _seeds.value = guestSeeds.toList()
                }
                _isLoading.value = false
                onSuccess()
            } else {
                val result = seedRepository.updateSeed(uid, seedId, title, description)
                result.fold(
                    onSuccess = {
                        _isLoading.value = false
                        onSuccess()
                    },
                    onFailure = {
                        _isLoading.value = false
                        // Error se maneja desde fuera
                    }
                )
            }
        }
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
