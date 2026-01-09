package com.example.seedlife.ui.seeddetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de detalle de una seed con lista de riegos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedDetailScreen(
    seedId: String,
    uid: String,
    isGuest: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    val viewModel: SeedDetailViewModel = viewModel(
        factory = SeedDetailViewModelFactory(uid, seedId, isGuest)
    )
    val seed by viewModel.seed.collectAsState()
    val waterings by viewModel.waterings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showWateringDialog by remember { mutableStateOf(false) }

    // Mostrar error si existe
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // El error se mostrará en el diálogo o en un snackbar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Semilla") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showWateringDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Regar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Información de la seed
            seed?.let { currentSeed ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = currentSeed.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = currentSeed.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nivel: ${currentSeed.level}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            currentSeed.lastWateredAt?.let { date ->
                                Text(
                                    text = "Último riego: ${formatDate(date)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Lista de riegos
            Text(
                text = "Historial de Riegos",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (waterings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay riegos registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(waterings) { watering ->
                        WateringItem(watering = watering)
                    }
                }
            }
        }
    }

    // Diálogo para añadir riego
    if (showWateringDialog) {
        AddWateringDialog(
            onDismiss = { showWateringDialog = false },
            onConfirm = { mood, note ->
                viewModel.addWatering(mood, note)
                showWateringDialog = false
            },
            isLoading = isLoading
        )
    }
}

/**
 * Item de riego en la lista
 */
@Composable
fun WateringItem(watering: Watering) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getMoodText(watering.getMoodEnum()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = getMoodColor(watering.getMoodEnum())
                )
                watering.date?.let { date ->
                    Text(
                        text = formatDate(date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                watering.note?.let { note ->
                    if (note.isNotBlank()) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Diálogo para añadir un nuevo riego
 */
@Composable
fun AddWateringDialog(
    onDismiss: () -> Unit,
    onConfirm: (WateringMood, String?) -> Unit,
    isLoading: Boolean
) {
    var selectedMood by remember { mutableStateOf<WateringMood?>(null) }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Regar Semilla") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Selecciona el estado de ánimo:")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WateringMood.values().forEach { mood ->
                        FilterChip(
                            selected = selectedMood == mood,
                            onClick = { selectedMood = mood },
                            label = { Text(getMoodText(mood)) }
                        )
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedMood?.let { mood ->
                        onConfirm(mood, note.takeIf { it.isNotBlank() })
                    }
                },
                enabled = selectedMood != null && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Factory para crear el ViewModel con parámetros
 */
class SeedDetailViewModelFactory(
    private val uid: String,
    private val seedId: String,
    private val isGuest: Boolean
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return SeedDetailViewModel(
            uid = uid,
            seedId = seedId,
            isGuest = isGuest
        ) as T
    }
}

// Funciones auxiliares
private fun getMoodText(mood: WateringMood): String {
    return when (mood) {
        WateringMood.GOOD -> "😊 Bien"
        WateringMood.OK -> "😐 Regular"
        WateringMood.BAD -> "😞 Mal"
    }
}

private fun getMoodColor(mood: WateringMood): Color {
    return when (mood) {
        WateringMood.GOOD -> Color(0xFF4CAF50)
        WateringMood.OK -> Color(0xFFFF9800)
        WateringMood.BAD -> Color(0xFFF44336)
    }
}

private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}
