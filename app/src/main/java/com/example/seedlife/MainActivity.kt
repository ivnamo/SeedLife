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
import androidx.navigation.compose.rememberNavController
import com.example.seedlife.data.FirestoreConfig
import com.example.seedlife.navigation.AppNavGraph
import com.example.seedlife.navigation.AuthNavGraph
import com.example.seedlife.ui.auth.AuthState
import com.example.seedlife.ui.auth.AuthViewModel
import com.example.seedlife.ui.session.SessionViewModel
import com.example.seedlife.ui.theme.SeedLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilitar persistencia offline de Firestore ANTES de cualquier uso
        FirestoreConfig.enablePersistence(this)
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
    val sessionViewModel: SessionViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val sessionState by sessionViewModel.sessionState.collectAsState()

    val authNavController = rememberNavController()
    val appNavController = rememberNavController()

    // Sincronizar AuthViewModel con SessionViewModel
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.isGuest) {
                    sessionViewModel.setGuestMode()
                } else {
                    sessionViewModel.setAuthenticatedUser(state.userId)
                }
            }
            else -> {
                // No hacer nada, mantener el estado actual
            }
        }
    }

    // Determinar qué gráfico mostrar
    val showApp = sessionState.uid != null || sessionState.isGuest

    if (showApp) {
        AppNavGraph(
            navController = appNavController,
            sessionViewModel = sessionViewModel,
            uid = sessionState.uid,
            isGuest = sessionState.isGuest,
            onSignOut = {
                sessionViewModel.signOut()
                authNavController.navigate(com.example.seedlife.navigation.AuthScreen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    } else {
        AuthNavGraph(
            navController = authNavController,
            authViewModel = authViewModel,
            onAuthSuccess = {
                // El estado se actualizará automáticamente por LaunchedEffect
            }
        )
    }
}