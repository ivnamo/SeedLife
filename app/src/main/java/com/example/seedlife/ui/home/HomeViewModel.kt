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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Date

/**
 * Filtros de búsqueda y ordenamiento
 */
data class SearchFilters(
    val query: String = "",
    val minLevel: Int? = null,
    val maxLevel: Int? = null,
    val sortBy: SortOption = SortOption.CREATED_DATE_DESC
)

/**
 * Opciones de ordenamiento
 */
enum class SortOption {
    CREATED_DATE_DESC,  // Más recientes primero
    CREATED_DATE_ASC,   // Más antiguas primero
    LEVEL_DESC,         // Mayor nivel primero
    LEVEL_ASC,          // Menor nivel primero
    TITLE_ASC,          // A-Z
    TITLE_DESC,         // Z-A
    LAST_WATERED_DESC,  // Regadas recientemente primero
    LAST_WATERED_ASC    // Regadas hace tiempo primero
}

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

    private val _searchFilters = MutableStateFlow(SearchFilters())
    val searchFilters: StateFlow<SearchFilters> = _searchFilters.asStateFlow()

    private val _filteredSeeds = MutableStateFlow<List<Seed>>(emptyList())
    val filteredSeeds: StateFlow<List<Seed>> = _filteredSeeds.asStateFlow()

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

        // Combinar seeds y filtros para aplicar búsqueda/filtrado
        viewModelScope.launch {
            combine(_uiState, _searchFilters) { state, filters ->
                when (state) {
                    is UiState.Success -> {
                        applyFilters(state.data, filters)
                    }
                    else -> emptyList()
                }
            }.collect { filtered ->
                _filteredSeeds.value = filtered
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

    /**
     * Aplica los filtros a la lista de seeds
     */
    private fun applyFilters(seeds: List<Seed>, filters: SearchFilters): List<Seed> {
        var result = seeds

        // Búsqueda por texto
        if (filters.query.isNotBlank()) {
            val queryLower = filters.query.lowercase()
            result = result.filter { seed ->
                seed.title.lowercase().contains(queryLower) ||
                seed.description.lowercase().contains(queryLower)
            }
        }

        // Filtro por nivel
        filters.minLevel?.let { min ->
            result = result.filter { it.level >= min }
        }
        filters.maxLevel?.let { max ->
            result = result.filter { it.level <= max }
        }

        // Ordenamiento
        result = when (filters.sortBy) {
            SortOption.CREATED_DATE_DESC -> result.sortedByDescending { it.createdAt ?: Date(0) }
            SortOption.CREATED_DATE_ASC -> result.sortedBy { it.createdAt ?: Date(0) }
            SortOption.LEVEL_DESC -> result.sortedByDescending { it.level }
            SortOption.LEVEL_ASC -> result.sortedBy { it.level }
            SortOption.TITLE_ASC -> result.sortedBy { it.title.lowercase() }
            SortOption.TITLE_DESC -> result.sortedByDescending { it.title.lowercase() }
            SortOption.LAST_WATERED_DESC -> result.sortedByDescending { it.lastWateredAt ?: Date(0) }
            SortOption.LAST_WATERED_ASC -> result.sortedBy { it.lastWateredAt ?: Date(0) }
        }

        return result
    }

    /**
     * Actualiza la consulta de búsqueda
     */
    fun updateSearchQuery(query: String) {
        _searchFilters.value = _searchFilters.value.copy(query = query)
    }

    /**
     * Actualiza el filtro de nivel
     */
    fun updateLevelFilter(minLevel: Int?, maxLevel: Int?) {
        _searchFilters.value = _searchFilters.value.copy(
            minLevel = minLevel,
            maxLevel = maxLevel
        )
    }

    /**
     * Actualiza la opción de ordenamiento
     */
    fun updateSortOption(sortOption: SortOption) {
        _searchFilters.value = _searchFilters.value.copy(sortBy = sortOption)
    }

    /**
     * Limpia todos los filtros
     */
    fun clearFilters() {
        _searchFilters.value = SearchFilters()
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
