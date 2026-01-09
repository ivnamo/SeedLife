package com.example.seedlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.seedlife.ui.auth.AuthScreen
import com.example.seedlife.ui.auth.AuthState
import com.example.seedlife.ui.auth.AuthViewModel
import com.example.seedlife.ui.home.HomeScreen
import com.example.seedlife.ui.theme.SeedLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SeedLifeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SeedLifeApp()
                }
            }
        }
    }
}

@Composable
fun SeedLifeApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val userData by authViewModel.userData.collectAsState()

    // Determinar qué pantalla mostrar
    when (val state = authState) {
        is AuthState.Success -> {
            HomeScreen(
                isGuest = state.isGuest,
                userName = state.user?.name,
                onSignOut = {
                    authViewModel.signOut()
                },
                authViewModel = if (!state.isGuest) authViewModel else null
            )
        }
        else -> {
            AuthScreen(
                onAuthSuccess = {
                    // La navegación se maneja automáticamente por el cambio de estado
                },
                viewModel = authViewModel
            )
        }
    }
}