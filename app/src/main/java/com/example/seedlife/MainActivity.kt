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
import com.example.seedlife.navigation.Screen
import com.example.seedlife.navigation.SeedLifeNavGraph
import com.example.seedlife.ui.auth.AuthState
import com.example.seedlife.ui.auth.AuthViewModel
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

    // Determinar la pantalla inicial y el estado
    val startDestination = when (val state = authState) {
        is AuthState.Success -> Screen.Home.route
        else -> Screen.Auth.route
    }

    val uid = when (val state = authState) {
        is AuthState.Success -> if (state.isGuest) "guest" else state.userId
        else -> null
    }

    val isGuest = when (val state = authState) {
        is AuthState.Success -> state.isGuest
        else -> false
    }

    SeedLifeNavGraph(
        startDestination = startDestination,
        authViewModel = authViewModel,
        uid = uid,
        isGuest = isGuest
    )
}