package com.example.seedlife.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.seedlife.data.model.Seed
import com.example.seedlife.ui.auth.AuthViewModel

/**
 * Pantalla principal con lista de seeds
 */
@Composable
fun HomeScreen(
    isGuest: Boolean = false,
    userName: String? = null,
    onSignOut: () -> Unit = {},
    authViewModel: AuthViewModel? = null,
    onSeedClick: (String) -> Unit = {},
    uid: String = "guest"
) {
    // Para modo invitado: seeds de ejemplo en memoria
    val guestSeeds = remember {
        if (isGuest) {
            listOf(
                Seed(id = "seed1", title = "Semilla de Ejemplo 1", description = "Una semilla de ejemplo", level = 1),
                Seed(id = "seed2", title = "Semilla de Ejemplo 2", description = "Otra semilla de ejemplo", level = 2)
            )
        } else {
            emptyList<Seed>()
        }
    }

    // TODO: En el futuro, aquí se observarán las seeds desde Firestore
    // Por ahora, usamos seeds de ejemplo para invitados
    val seeds = if (isGuest) guestSeeds else emptyList<Seed>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Jardín") },
                actions = {
                    if (authViewModel != null && !isGuest) {
                        TextButton(onClick = {
                            authViewModel.signOut()
                            onSignOut()
                        }) {
                            Text("Cerrar Sesión")
                        }
                    }
                }
            )
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
                            onClick = { onSeedClick(seed.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SeedItem(
    seed: Seed,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nivel: ${seed.level}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
