package com.example.seedlife.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seedlife.ui.auth.AuthViewModel

/**
 * Pantalla principal (placeholder)
 */
@Composable
fun HomeScreen(
    isGuest: Boolean = false,
    userName: String? = null,
    onSignOut: () -> Unit = {},
    authViewModel: AuthViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Mi Jardín",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isGuest) {
            Text(
                text = "Modo Invitado",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            userName?.let {
                Text(
                    text = "Bienvenido, $it",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (authViewModel != null && !isGuest) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    authViewModel.signOut()
                    onSignOut()
                }
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}
