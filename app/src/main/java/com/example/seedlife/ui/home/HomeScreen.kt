package com.example.seedlife.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seedlife.data.model.Seed
import com.example.seedlife.ui.auth.AuthViewModel

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
    val seeds by homeViewModel.seeds.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf<String?>(null) }
    var expandedSeedId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Jardín") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditSeed("") } // seedId vacío = crear nueva
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Semilla")
            }
        }
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

            if (seeds.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay semillas",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "En el futuro podrás crear semillas aquí",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(seeds) { seed ->
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
    }

    // Diálogo de confirmación para eliminar
    showDeleteDialog?.let { seedId ->
        val seed = seeds.find { it.id == seedId }
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
