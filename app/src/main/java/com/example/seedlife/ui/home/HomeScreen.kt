package com.example.seedlife.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seedlife.R
import com.example.seedlife.data.model.Seed
import com.example.seedlife.ui.auth.AuthViewModel
import com.example.seedlife.ui.common.UiState

/**
 * Pantalla principal con lista de seeds
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isGuest: Boolean = false,
    userName: String? = null,
    onSignOut: () -> Unit = {},
    authViewModel: AuthViewModel? = null,
    onSeedClick: (String) -> Unit = {},
    onEditSeed: (String) -> Unit = {},
    uid: String = "guest"
) {
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(uid, isGuest)
    )
    val uiState by homeViewModel.uiState.collectAsState()
    val filteredSeeds by homeViewModel.filteredSeeds.collectAsState()
    val searchFilters by homeViewModel.searchFilters.collectAsState()
    val snackbarMessage by homeViewModel.snackbarMessage.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var expandedSeedId by remember { mutableStateOf<String?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf(TextFieldValue(searchFilters.query)) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Sincronizar searchText con filtros
    LaunchedEffect(searchFilters.query) {
        if (searchText.text != searchFilters.query) {
            searchText = TextFieldValue(searchFilters.query)
        }
    }

    // Mostrar snackbar cuando hay mensaje
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            homeViewModel.clearSnackbarMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (showSearchBar) {
                        SearchBar(
                            searchText = searchText,
                            onSearchTextChange = {
                                searchText = it
                                homeViewModel.updateSearchQuery(it.text)
                            },
                            onClose = {
                                showSearchBar = false
                                homeViewModel.updateSearchQuery("")
                            }
                        )
                    } else {
                        Text("Mi Jardín")
                    }
                },
                actions = {
                    if (!showSearchBar) {
                        IconButton(onClick = { showSearchBar = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        }
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditSeed("") } // seedId vacío = crear nueva
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Semilla")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isGuest) {
                Text(
                    text = "Modo Invitado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                userName?.let {
                    Text(
                        text = "Bienvenido, $it",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Mostrar indicador de filtros activos
            if (searchFilters.query.isNotBlank() ||
                searchFilters.minLevel != null ||
                searchFilters.maxLevel != null) {
                FilterChips(
                    filters = searchFilters,
                    onClear = { homeViewModel.clearFilters() },
                    onRemoveQuery = { homeViewModel.updateSearchQuery("") },
                    onRemoveLevelFilter = { homeViewModel.updateLevelFilter(null, null) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when (val state = uiState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // Imagen del jardín vacío
                                Image(
                                    painter = painterResource(id = R.drawable.empty_garden),
                                    contentDescription = "Jardín vacío",
                                    modifier = Modifier
                                        .size(250.dp)
                                        .padding(bottom = 24.dp)
                                )
                                
                                // Texto centrado
                                Text(
                                    text = "No hay semillas",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Toca el botón + para crear tu primera semilla",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else if (filteredSeeds.isEmpty() && state.data.isNotEmpty()) {
                        // Hay seeds pero no coinciden con filtros
                        EmptySearchState(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredSeeds) { seed ->
                                SeedItem(
                                    seed = seed,
                                    onClick = { onSeedClick(seed.id) },
                                    onEdit = { onEditSeed(seed.id) },
                                    onDelete = { showDeleteDialog = seed.id },
                                    isExpanded = expandedSeedId == seed.id,
                                    onExpandChange = { expandedSeedId = if (expandedSeedId == seed.id) null else seed.id }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(
                                onClick = { state.retry?.invoke() }
                            ) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { seedId ->
        val seed = filteredSeeds.find { it.id == seedId }
        DeleteSeedDialog(
            seedTitle = seed?.title ?: "esta semilla",
            onConfirm = {
                homeViewModel.deleteSeed(seedId) {
                    showDeleteDialog = null
                }
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    // Diálogo de filtros
    if (showFilterDialog) {
        FilterDialog(
            currentFilters = searchFilters,
            onApplyFilters = { minLevel, maxLevel, sortBy ->
                homeViewModel.updateLevelFilter(minLevel, maxLevel)
                homeViewModel.updateSortOption(sortBy)
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun SeedItem(
    seed: Seed,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isExpanded: Boolean,
    onExpandChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onClick)
                ) {
                    Text(
                        text = seed.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = seed.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Nivel: ${seed.level}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                IconButton(onClick = onExpandChange) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                }
            }

            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = onExpandChange
            ) {
                DropdownMenuItem(
                    text = { Text("Editar") },
                    onClick = {
                        onExpandChange()
                        onEdit()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        onExpandChange()
                        onDelete()
                    }
                )
            }
        }
    }
}

// Diálogo de confirmación para eliminar
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

// Barra de búsqueda
@Composable
fun SearchBar(
    searchText: TextFieldValue,
    onSearchTextChange: (TextFieldValue) -> Unit,
    onClose: () -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar semillas...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (searchText.text.isNotBlank()) {
                IconButton(onClick = {
                    onSearchTextChange(TextFieldValue(""))
                    onClose()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { /* Ya filtra en tiempo real */ }
        )
    )
}

// Chips de filtros activos
@Composable
fun FilterChips(
    filters: SearchFilters,
    onClear: () -> Unit,
    onRemoveQuery: () -> Unit,
    onRemoveLevelFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (filters.query.isNotBlank()) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text("Buscar: ${filters.query}") },
                trailingIcon = {
                    IconButton(onClick = onRemoveQuery) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Quitar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        }
        if (filters.minLevel != null || filters.maxLevel != null) {
            FilterChip(
                selected = true,
                onClick = { },
                label = {
                    Text(
                        when {
                            filters.minLevel != null && filters.maxLevel != null ->
                                "Nivel ${filters.minLevel}-${filters.maxLevel}"
                            filters.minLevel != null -> "Nivel ≥${filters.minLevel}"
                            else -> "Nivel ≤${filters.maxLevel}"
                        }
                    )
                },
                trailingIcon = {
                    IconButton(onClick = onRemoveLevelFilter) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Quitar",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onClear) {
            Text("Limpiar")
        }
    }
}

// Diálogo de filtros
@Composable
fun FilterDialog(
    currentFilters: SearchFilters,
    onApplyFilters: (Int?, Int?, SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    var minLevel by remember { mutableStateOf(currentFilters.minLevel?.toString() ?: "") }
    var maxLevel by remember { mutableStateOf(currentFilters.maxLevel?.toString() ?: "") }
    var sortBy by remember { mutableStateOf(currentFilters.sortBy) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar y Ordenar") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Filtros de nivel
                Text("Nivel", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = minLevel,
                        onValueChange = { minLevel = it },
                        label = { Text("Mínimo") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = maxLevel,
                        onValueChange = { maxLevel = it },
                        label = { Text("Máximo") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                HorizontalDivider()

                // Ordenamiento
                Text("Ordenar por", style = MaterialTheme.typography.labelLarge)
                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { sortBy = option },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sortBy == option,
                            onClick = { sortBy = option }
                        )
                        Text(
                            text = when (option) {
                                SortOption.CREATED_DATE_DESC -> "Más recientes primero"
                                SortOption.CREATED_DATE_ASC -> "Más antiguas primero"
                                SortOption.LEVEL_DESC -> "Mayor nivel primero"
                                SortOption.LEVEL_ASC -> "Menor nivel primero"
                                SortOption.TITLE_ASC -> "Título A-Z"
                                SortOption.TITLE_DESC -> "Título Z-A"
                                SortOption.LAST_WATERED_DESC -> "Regadas recientemente"
                                SortOption.LAST_WATERED_ASC -> "Regadas hace tiempo"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onApplyFilters(
                    minLevel.toIntOrNull(),
                    maxLevel.toIntOrNull(),
                    sortBy
                )
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Estado vacío cuando hay filtros activos pero no hay resultados
@Composable
fun EmptySearchState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No se encontraron semillas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Intenta ajustar los filtros de búsqueda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
