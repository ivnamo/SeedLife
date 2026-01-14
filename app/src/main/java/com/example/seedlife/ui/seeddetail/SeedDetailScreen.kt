package com.example.seedlife.ui.seeddetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.seedlife.R
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.example.seedlife.ui.common.UiState
import com.example.seedlife.util.ValidationUtils
import java.io.File
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
    onNavigateBack: () -> Unit = {},
    onEditSeed: (String) -> Unit = {},
    onDeleteSeed: (String) -> Unit = {}
) {
    val viewModel: SeedDetailViewModel = viewModel(
        factory = SeedDetailViewModelFactory(uid, seedId, isGuest)
    )
    val seedState by viewModel.seed.collectAsState()
    val wateringsState by viewModel.waterings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    val context = LocalContext.current
    var showWateringDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Launcher para tomar foto
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.uploadSeedPhoto(imageUri!!)
        }
    }

    // Función helper para generar Uri temporal
    fun getImageUri(): Uri? {
        return try {
            val imageFile = File(context.cacheDir, "seed_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )
        } catch (e: Exception) {
            null
        }
    }

    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
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
                },
                actions = {
                    TextButton(onClick = { onEditSeed(seedId) }) {
                        Text("Editar")
                    }
                    TextButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Información de la seed
            when (val state = seedState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    state.data?.let { currentSeed ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Imagen de la seed (header)
                                val imageUrl = currentSeed.photoUrl
                                    ?: "https://picsum.photos/seed/${currentSeed.id}/600/400"
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .padding(bottom = 16.dp)
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Foto de ${currentSeed.title}",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(id = R.drawable.seed_bag),
                                        error = painterResource(id = R.drawable.seed_bag)
                                    )
                                }

                                // Botón para añadir foto
                                Button(
                                    onClick = {
                                        if (isGuest) {
                                            snackbarHostState.showSnackbar("Solo disponible con cuenta")
                                        } else {
                                            val uri = getImageUri()
                                            if (uri != null) {
                                                imageUri = uri
                                                takePictureLauncher.launch(uri)
                                            }
                                        }
                                    },
                                    enabled = !isLoading && !isGuest,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text("Añadir foto")
                                }

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
                }
                is UiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { state.retry?.invoke() }) {
                                Text("Reintentar")
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

            when (val state = wateringsState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay riegos registrados aún.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data) { watering ->
                                WateringItem(watering = watering)
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { state.retry?.invoke() }) {
                                Text("Reintentar")
                            }
                        }
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

    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        val seedTitle = when (val state = seedState) {
            is UiState.Success -> state.data?.title ?: "esta semilla"
            else -> "esta semilla"
        }
        DeleteSeedDialog(
            seedTitle = seedTitle,
            onConfirm = {
                viewModel.deleteSeed {
                    onDeleteSeed(seedId) // Notify parent to navigate back
                }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
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
    var noteError by remember { mutableStateOf<String?>(null) }

    // Validar note en tiempo real
    LaunchedEffect(note) {
        val validation = ValidationUtils.validateWateringNote(note)
        noteError = validation.errorMessage
    }

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
                    onValueChange = { 
                        note = it
                        noteError = null // Limpiar error al escribir
                    },
                    label = { Text("Nota (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    isError = noteError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (noteError != null) {
                                Text(noteError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.width(0.dp))
                            }
                            Text(
                                text = "${note.length}/250",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedMood?.let { mood ->
                        val validation = ValidationUtils.validateWateringNote(note)
                        if (validation.isValid) {
                            onConfirm(mood, note.takeIf { it.isNotBlank() })
                        }
                    }
                },
                enabled = selectedMood != null && !isLoading && noteError == null
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

@Composable
fun DeleteSeedDialog(
    seedTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar Semilla") },
        text = { Text("¿Estás seguro de que quieres eliminar \"$seedTitle\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(date)
}
