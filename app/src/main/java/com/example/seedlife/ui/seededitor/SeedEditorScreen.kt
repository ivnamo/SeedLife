package com.example.seedlife.ui.seededitor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.seedlife.util.ValidationUtils

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
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()

    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Validar título en tiempo real
    LaunchedEffect(title) {
        if (title.isNotBlank()) {
            val validation = ValidationUtils.validateSeedTitle(title)
            titleError = validation.errorMessage
        } else {
            titleError = null
        }
    }

    // Validar descripción en tiempo real
    LaunchedEffect(description) {
        val validation = ValidationUtils.validateSeedDescription(description)
        descriptionError = validation.errorMessage
    }

    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbarMessage()
        }
    }

    // Mostrar error si existe
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                onValueChange = { 
                    viewModel.updateTitle(it)
                    titleError = null // Limpiar error al escribir
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { 
                        viewModel.updateDescription(it)
                        descriptionError = null // Limpiar error al escribir
                    },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    maxLines = 10,
                    isError = descriptionError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (descriptionError != null) {
                                Text(descriptionError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Spacer(modifier = Modifier.width(0.dp))
                            }
                            Text(
                                text = "${description.length}/200",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
            }

            Button(
                onClick = {
                    // Validar antes de guardar
                    val titleValidation = ValidationUtils.validateSeedTitle(title)
                    val descValidation = ValidationUtils.validateSeedDescription(description)
                    
                    if (titleValidation.isValid && descValidation.isValid) {
                        viewModel.save {
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && 
                         title.isNotBlank() && 
                         titleError == null && 
                         descriptionError == null
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

    private val _snackbarMessage = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val snackbarMessage: kotlinx.coroutines.flow.StateFlow<String?> = _snackbarMessage.asStateFlow()

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
                    _snackbarMessage.value = if (seedId == null) "Semilla creada" else "Semilla actualizada"
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

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
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
