package com.example.seedlife.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.seedlife.data.repository.StatsRepository
import com.example.seedlife.data.repository.UserStats
import com.example.seedlife.ui.session.SessionViewModel

/**
 * Pantalla de estadísticas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    sessionViewModel: SessionViewModel = viewModel(),
    statsRepository: StatsRepository = StatsRepository()
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()
    val statsViewModel: StatsViewModel = viewModel(
        factory = StatsViewModelFactory(
            uid = sessionState.uid,
            isGuest = sessionState.isGuest,
            statsRepository = statsRepository
        )
    )

    val stats by statsViewModel.stats.collectAsState()
    val isLoading by statsViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (sessionState.isGuest) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No disponible en modo invitado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Inicia sesión para ver tus estadísticas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                StatsCard(
                    title = "Total de Semillas",
                    value = stats.totalSeeds.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                StatsCard(
                    title = "Total de Riegos",
                    value = stats.totalWaterings.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * ViewModel para estadísticas
 */
class StatsViewModel(
    private val uid: String?,
    private val isGuest: Boolean,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(
        UserStats()
    )
    val stats: StateFlow<UserStats> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        if (!isGuest && uid != null) {
            viewModelScope.launch {
                statsRepository.observeStats(uid).collect { stats ->
                    _stats.value = stats
                    _isLoading.value = false
                }
            }
            
            // Calcular stats iniciales
            viewModelScope.launch {
                _isLoading.value = true
                val calculatedStats = statsRepository.calculateStats(uid)
                _stats.value = calculatedStats
                // Actualizar en Firestore
                statsRepository.updateStats(uid, calculatedStats.totalSeeds, calculatedStats.totalWaterings)
            }
        }
    }
}

/**
 * Factory para StatsViewModel
 */
class StatsViewModelFactory(
    private val uid: String?,
    private val isGuest: Boolean,
    private val statsRepository: StatsRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatsViewModel(uid, isGuest, statsRepository) as T
    }
}
