package com.example.seedlife.ui.seededitor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla para crear o editar una seed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedEditorScreen(
    seedId: String? = null, // null = crear, no null = editar
    uid: String,
    isGuest: Boolean = false,
    onNavigateBack: () -> Unit,
    viewModel: SeedEditorViewModel = viewModel(
        factory = SeedEditorViewModelFactory(seedId, uid, isGuest)
    )
) {
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showError = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (seedId == null) "Nueva Semilla" else "Editar Semilla") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                enabled = !isLoading,
                maxLines = 10
            )

            Button(
                onClick = {
                    viewModel.save {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && title.isNotBlank()
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
        }
    }

    // Mostrar error si existe
    if (showError && errorMessage != null) {
        AlertDialog(
            onDismissRequest = {
                showError = false
                viewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                TextButton(onClick = {
                    showError = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * ViewModel para editar/crear seed
 */
class SeedEditorViewModel(
    private val seedId: String?,
    private val uid: String,
    private val isGuest: Boolean,
    private val seedRepository: com.example.seedlife.data.repository.SeedRepository = com.example.seedlife.data.repository.SeedRepository()
) : androidx.lifecycle.ViewModel() {

    private val _title = kotlinx.coroutines.flow.MutableStateFlow("")
    val title: kotlinx.coroutines.flow.StateFlow<String> = _title.asStateFlow()

    private val _description = kotlinx.coroutines.flow.MutableStateFlow("")
    val description: kotlinx.coroutines.flow.StateFlow<String> = _description.asStateFlow()

    private val _isLoading = kotlinx.coroutines.flow.MutableStateFlow(false)
    val isLoading: kotlinx.coroutines.flow.StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val errorMessage: kotlinx.coroutines.flow.StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Si estamos editando, cargar los datos de la seed
        if (seedId != null && !isGuest) {
            viewModelScope.launch {
                seedRepository.observeSeed(uid, seedId).collect { seed ->
                    seed?.let {
                        _title.value = it.title
                        _description.value = it.description
                    }
                }
            }
        } else if (seedId != null && isGuest) {
            // Para invitado, los datos se pasan desde fuera
            // Por ahora, dejamos vacío
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateDescription(newDescription: String) {
        _description.value = newDescription
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = if (seedId == null) {
                // Crear nueva seed
                if (isGuest) {
                    // Para invitado, se maneja desde fuera (retornar éxito)
                    Result.success("guest_seed_${System.currentTimeMillis()}")
                } else {
                    seedRepository.createSeed(uid, _title.value, _description.value)
                }
            } else {
                // Actualizar seed existente
                if (isGuest) {
                    // Para invitado, se maneja desde fuera (retornar éxito)
                    Result.success(Unit)
                } else {
                    seedRepository.updateSeed(uid, seedId, _title.value, _description.value)
                }
            }

            result.fold(
                onSuccess = {
                    _isLoading.value = false
                    onSuccess()
                },
                onFailure = { exception ->
                    _isLoading.value = false
                    _errorMessage.value = exception.message ?: "Error al guardar"
                }
            )
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * Factory para SeedEditorViewModel
 */
class SeedEditorViewModelFactory(
    private val seedId: String?,
    private val uid: String,
    private val isGuest: Boolean
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return SeedEditorViewModel(seedId, uid, isGuest) as T
    }
}
