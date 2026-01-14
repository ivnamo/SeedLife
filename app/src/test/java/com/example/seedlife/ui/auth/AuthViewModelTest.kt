package com.example.seedlife.ui.auth

import app.cash.turbine.test
import com.example.seedlife.data.model.User
import com.example.seedlife.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para AuthViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    
    private lateinit var mockRepository: AuthRepository
    private lateinit var viewModel: AuthViewModel
    
    @Before
    fun setup() {
        mockRepository = mockk<AuthRepository>(relaxed = true)
        // Mockear el comportamiento del repositorio para evitar llamadas reales en init
        every { mockRepository.isUserLoggedIn() } returns false
        viewModel = AuthViewModel(mockRepository)
    }
    
    // ========== Tests de Login ==========
    
    @Test
    fun `login exitoso actualiza estado a Success`() = runTest {
        // Given
        val email = "test@ejemplo.com"
        val password = "password123"
        val uid = "user123"
        val user = User(name = "Test User", email = email)
        
        coEvery { mockRepository.login(email, password) } returns Result.success(uid)
        coEvery { mockRepository.getUserData(uid) } returns Result.success(user)
        
        // When
        viewModel.login(email, password)
        
        // Then - esperar un poco para que las coroutines se ejecuten
        delay(100)
        viewModel.authState.test {
            // Skip el estado inicial (Idle)
            skipItems(1)
            // El tipo Loading está garantizado después de skipItems(1)
            awaitItem() as AuthState.Loading
            
            val successState = awaitItem() as AuthState.Success
            assertEquals(uid, successState.userId)
            assertEquals(user, successState.user)
            assertFalse(successState.isGuest)
        }
        
        coVerify { mockRepository.login(email, password) }
        coVerify { mockRepository.getUserData(uid) }
    }
    
    @Test
    fun `login fallido actualiza estado a Error`() = runTest {
        // Given
        val email = "test@ejemplo.com"
        val password = "wrongpassword"
        val errorMessage = "Credenciales inválidas"
        
        coEvery { mockRepository.login(email, password) } returns Result.failure(
            Exception(errorMessage)
        )
        
        // When
        viewModel.login(email, password)
        
        // Then - esperar un poco para que las coroutines se ejecuten
        delay(100)
        viewModel.authState.test {
            // Skip el estado inicial (Idle)
            skipItems(1)
            // El tipo Loading está garantizado después de skipItems(1)
            awaitItem() as AuthState.Loading
            
            val errorState = awaitItem() as AuthState.Error
            assertEquals(errorMessage, errorState.message)
        }
        
        coVerify { mockRepository.login(email, password) }
        coVerify(exactly = 0) { mockRepository.getUserData(any()) }
    }
    
    // ========== Tests de Registro ==========
    
    @Test
    fun `registro exitoso actualiza estado a Success`() = runTest {
        // Given
        val email = "nuevo@ejemplo.com"
        val password = "password123"
        val name = "Nuevo Usuario"
        val uid = "newuser123"
        val user = User(name = name, email = email)
        
        coEvery { mockRepository.register(email, password, name) } returns Result.success(uid)
        coEvery { mockRepository.getUserData(uid) } returns Result.success(user)
        
        // When
        viewModel.register(email, password, name)
        
        // Then - esperar un poco para que las coroutines se ejecuten
        delay(100)
        viewModel.authState.test {
            // Skip el estado inicial (Idle)
            skipItems(1)
            // El tipo Loading está garantizado después de skipItems(1)
            awaitItem() as AuthState.Loading
            
            val successState = awaitItem() as AuthState.Success
            assertEquals(uid, successState.userId)
            assertEquals(user, successState.user)
            assertFalse(successState.isGuest)
        }
        
        coVerify { mockRepository.register(email, password, name) }
        coVerify { mockRepository.getUserData(uid) }
    }
    
    @Test
    fun `registro fallido actualiza estado a Error`() = runTest {
        // Given
        val email = "existente@ejemplo.com"
        val password = "password123"
        val name = "Usuario"
        val errorMessage = "El email ya está en uso"
        
        coEvery { mockRepository.register(email, password, name) } returns Result.failure(
            Exception(errorMessage)
        )
        
        // When
        viewModel.register(email, password, name)
        
        // Then - esperar un poco para que las coroutines se ejecuten
        delay(100)
        viewModel.authState.test {
            // Skip el estado inicial (Idle)
            skipItems(1)
            // El tipo Loading está garantizado después de skipItems(1)
            awaitItem() as AuthState.Loading
            
            val errorState = awaitItem() as AuthState.Error
            assertEquals(errorMessage, errorState.message)
        }
        
        coVerify { mockRepository.register(email, password, name) }
        coVerify(exactly = 0) { mockRepository.getUserData(any()) }
    }
    
    // ========== Tests de Guest ==========
    
    @Test
    fun `enterAsGuest actualiza estado a Success con isGuest true`() {
        // When
        viewModel.enterAsGuest()
        
        // Then
        val state = viewModel.authState.value as AuthState.Success
        assertEquals("guest", state.userId)
        assertNull(state.user)
        assertTrue(state.isGuest)
        
        coVerify(exactly = 0) { mockRepository.register(any(), any(), any()) }
        coVerify(exactly = 0) { mockRepository.login(any(), any()) }
    }
    
    // ========== Tests de SignOut ==========
    
    @Test
    fun `signOut resetea estado a Idle`() {
        // Given - primero entrar como guest
        viewModel.enterAsGuest()
        
        // When
        viewModel.signOut()
        
        // Then
        val state = viewModel.authState.value
        assertTrue(state is AuthState.Idle) // Verificación necesaria para confirmar el tipo
        
        coVerify { mockRepository.signOut() }
    }
    
    // ========== Tests de ClearError ==========
    
    @Test
    fun `clearError resetea estado de Error a Idle`() = runTest {
        // Given - crear un error
        val email = "test@ejemplo.com"
        val password = "wrong"
        
        coEvery { mockRepository.login(email, password) } returns Result.failure(
            Exception("Error de prueba")
        )
        
        viewModel.login(email, password)
        
        // Esperar a que el error se establezca
        delay(100)
        var currentState = viewModel.authState.value
        while (currentState !is AuthState.Error) {
            delay(50)
            currentState = viewModel.authState.value
        }
        // currentState ya es AuthState.Error después del while
        
        // When
        viewModel.clearError()
        
        // Then
        val finalState = viewModel.authState.value
        assertTrue(finalState is AuthState.Idle) // Verificación necesaria para confirmar el tipo
    }
    
    @Test
    fun `clearError no hace nada si el estado no es Error`() {
        // Given - estado Idle
        val initialState = viewModel.authState.value
        assertTrue(initialState is AuthState.Idle) // Verificación necesaria para confirmar el tipo
        
        // When
        viewModel.clearError()
        
        // Then - el estado debe seguir siendo Idle
        val finalState = viewModel.authState.value
        assertTrue(finalState is AuthState.Idle) // Verificación necesaria para confirmar el tipo
    }
}
