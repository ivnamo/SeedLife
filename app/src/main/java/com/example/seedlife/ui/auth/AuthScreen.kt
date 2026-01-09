package com.example.seedlife.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla de autenticación con Login, Registro y modo Invitado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    // Navegar a HomeScreen cuando la autenticación sea exitosa
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onAuthSuccess()
        }
    }

    // Mostrar error si existe (usando variable local para smart cast)
    val currentState = authState
    val errorMessage = when (currentState) {
        is AuthState.Error -> currentState.message
        else -> null
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            // El error se mostrará en la UI
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLoginMode) "Iniciar Sesión" else "Registrarse",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (!isLoginMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { newName -> name = newName },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = authState !is AuthState.Loading
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> email = newEmail },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = authState !is AuthState.Loading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newPassword -> password = newPassword },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            enabled = authState !is AuthState.Loading
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                viewModel.clearError()
                // Limpiar espacios en blanco de los campos
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()
                val trimmedName = name.trim()
                
                if (isLoginMode) {
                    viewModel.login(trimmedEmail, trimmedPassword)
                } else {
                    if (trimmedName.isNotBlank()) {
                        viewModel.register(trimmedEmail, trimmedPassword, trimmedName)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = authState !is AuthState.Loading && 
                     email.isNotBlank() && 
                     password.isNotBlank() &&
                     (isLoginMode || name.isNotBlank())
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (isLoginMode) "Iniciar Sesión" else "Registrarse")
            }
        }

        TextButton(
            onClick = { isLoginMode = !isLoginMode },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = if (isLoginMode) "¿No tienes cuenta? Regístrate" 
                       else "¿Ya tienes cuenta? Inicia sesión"
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        OutlinedButton(
            onClick = {
                viewModel.clearError()
                viewModel.enterAsGuest()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            Text("Entrar como invitado")
        }
    }
}
