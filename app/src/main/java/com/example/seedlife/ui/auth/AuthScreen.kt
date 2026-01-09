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
import com.example.seedlife.util.ValidationUtils

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
    var confirmPassword by remember { mutableStateOf("") }

    // Estados de validación
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()

    // Validar email en tiempo real
    LaunchedEffect(email) {
        if (email.isNotBlank()) {
            emailError = if (ValidationUtils.isValidEmail(email)) {
                null
            } else {
                "El formato del email no es válido"
            }
        } else {
            emailError = null
        }
    }

    // Validar password en tiempo real
    LaunchedEffect(password) {
        if (password.isNotBlank()) {
            passwordError = if (ValidationUtils.isValidPassword(password)) {
                null
            } else {
                "La contraseña debe tener al menos 6 caracteres"
            }
        } else {
            passwordError = null
        }
    }

    // Validar confirmación de password en tiempo real (solo en registro)
    LaunchedEffect(confirmPassword, password) {
        if (!isLoginMode && confirmPassword.isNotBlank()) {
            confirmPasswordError = if (confirmPassword == password) {
                null
            } else {
                "Las contraseñas no coinciden"
            }
        } else {
            confirmPasswordError = null
        }
    }

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
                enabled = authState !is AuthState.Loading,
                isError = name.isBlank() && !isLoginMode,
                supportingText = if (name.isBlank() && !isLoginMode) {
                    { Text("El nombre es obligatorio") }
                } else null
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { newEmail -> 
                email = newEmail
                emailError = null // Limpiar error al escribir
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (emailError != null) 4.dp else 16.dp),
            enabled = authState !is AuthState.Loading,
            singleLine = true,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newPassword -> 
                password = newPassword
                passwordError = null // Limpiar error al escribir
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (passwordError != null) 4.dp else if (isLoginMode) 24.dp else 16.dp),
            enabled = authState !is AuthState.Loading,
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it) } }
        )

        if (!isLoginMode) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { newConfirmPassword -> 
                    confirmPassword = newConfirmPassword
                    confirmPasswordError = null // Limpiar error al escribir
                },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (confirmPasswordError != null) 4.dp else 24.dp),
                enabled = authState !is AuthState.Loading,
                isError = confirmPasswordError != null,
                supportingText = confirmPasswordError?.let { { Text(it) } }
            )
        }

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
                
                // Validar antes de enviar
                val isEmailValid = ValidationUtils.isValidEmail(trimmedEmail)
                val isPasswordValid = ValidationUtils.isValidPassword(trimmedPassword)
                val isNameValid = isLoginMode || trimmedName.isNotBlank()
                val isConfirmPasswordValid = isLoginMode || confirmPassword == password

                if (isEmailValid && isPasswordValid && isNameValid && isConfirmPasswordValid) {
                    if (isLoginMode) {
                        viewModel.login(trimmedEmail, trimmedPassword)
                    } else {
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
                     (isLoginMode || (name.isNotBlank() && confirmPassword.isNotBlank())) &&
                     emailError == null &&
                     passwordError == null &&
                     (isLoginMode || confirmPasswordError == null)
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
