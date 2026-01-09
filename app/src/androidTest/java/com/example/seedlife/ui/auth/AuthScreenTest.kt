package com.example.seedlife.ui.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.seedlife.data.repository.AuthRepository
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentados para AuthScreen
 */
@RunWith(AndroidJUnit4::class)
class AuthScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `pantalla de login muestra campos de email y contraseña`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        var authSuccessCalled = false
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = { authSuccessCalled = true },
                viewModel = viewModel
            )
        }
        
        // Then
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate").assertIsDisplayed()
        composeTestRule.onNodeWithText("Entrar como invitado").assertIsDisplayed()
    }
    
    @Test
    fun `pantalla de registro muestra campo de nombre adicional`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Cambiar a modo registro
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()
        
        // Then
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Contraseña").assertIsDisplayed()
    }
    
    @Test
    fun `botón de login está deshabilitado cuando campos están vacíos`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Then
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsNotEnabled()
    }
    
    @Test
    fun `botón de login se habilita cuando email y contraseña tienen contenido`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Ingresar email
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@ejemplo.com")
        
        // Ingresar contraseña
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("password123")
        
        // Then
        composeTestRule.onNodeWithText("Iniciar Sesión")
            .assertIsEnabled()
    }
    
    @Test
    fun `botón de registro está deshabilitado cuando nombre está vacío`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Cambiar a modo registro
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()
        
        // Ingresar solo email y contraseña (sin nombre)
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@ejemplo.com")
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("password123")
        
        // Then
        composeTestRule.onNodeWithText("Registrarse")
            .assertIsNotEnabled()
    }
    
    @Test
    fun `botón de registro se habilita cuando todos los campos están completos`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Cambiar a modo registro
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()
        
        // Completar todos los campos
        composeTestRule.onNodeWithText("Nombre")
            .performTextInput("Usuario Test")
        composeTestRule.onNodeWithText("Email")
            .performTextInput("test@ejemplo.com")
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("password123")
        
        // Then
        composeTestRule.onNodeWithText("Registrarse")
            .assertIsEnabled()
    }
    
    @Test
    fun `cambiar entre login y registro actualiza la UI correctamente`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Verificar modo login inicial
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre").assertDoesNotExist()
        
        // Cambiar a registro
        composeTestRule.onNodeWithText("¿No tienes cuenta? Regístrate")
            .performClick()
        
        // Verificar modo registro
        composeTestRule.onNodeWithText("Registrarse").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre").assertIsDisplayed()
        
        // Volver a login
        composeTestRule.onNodeWithText("¿Ya tienes cuenta? Inicia sesión")
            .performClick()
        
        // Verificar modo login de nuevo
        composeTestRule.onNodeWithText("Iniciar Sesión").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nombre").assertDoesNotExist()
    }
    
    @Test
    fun `botón entrar como invitado está siempre habilitado cuando no hay loading`() {
        // Given
        val mockRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = AuthViewModel(mockRepository)
        
        // When
        composeTestRule.setContent {
            AuthScreen(
                onAuthSuccess = {},
                viewModel = viewModel
            )
        }
        
        // Then
        composeTestRule.onNodeWithText("Entrar como invitado")
            .assertIsEnabled()
    }
}
