package com.example.seedlife.util

import org.junit.Test
import org.junit.Assert.*

/**
 * Tests unitarios para ValidationUtils
 */
class ValidationUtilsTest {

    // ========== Tests de validación de Email ==========
    
    @Test
    fun `email válido debe pasar validación`() {
        assertTrue(ValidationUtils.isValidEmail("usuario@ejemplo.com"))
        assertTrue(ValidationUtils.isValidEmail("test.email@domain.co.uk"))
        assertTrue(ValidationUtils.isValidEmail("user+tag@example.org"))
    }
    
    @Test
    fun `email inválido debe fallar validación`() {
        assertFalse(ValidationUtils.isValidEmail("email-invalido"))
        assertFalse(ValidationUtils.isValidEmail("@dominio.com"))
        assertFalse(ValidationUtils.isValidEmail("usuario@"))
        assertFalse(ValidationUtils.isValidEmail("usuario@dominio"))
        assertFalse(ValidationUtils.isValidEmail("sin arroba"))
    }
    
    @Test
    fun `email vacío debe fallar validación`() {
        assertFalse(ValidationUtils.isValidEmail(""))
        assertFalse(ValidationUtils.isValidEmail("   "))
        assertFalse(ValidationUtils.isValidEmail("\t\n"))
    }
    
    @Test
    fun `email con espacios debe fallar validación`() {
        assertFalse(ValidationUtils.isValidEmail("usuario @ejemplo.com"))
        assertFalse(ValidationUtils.isValidEmail("usuario@ejemplo .com"))
    }
    
    // ========== Tests de validación de Contraseña ==========
    
    @Test
    fun `contraseña válida debe pasar validación`() {
        assertTrue(ValidationUtils.isValidPassword("password123", 6))
        assertTrue(ValidationUtils.isValidPassword("123456", 6))
        assertTrue(ValidationUtils.isValidPassword("muyLarga123", 6))
    }
    
    @Test
    fun `contraseña corta debe fallar validación`() {
        assertFalse(ValidationUtils.isValidPassword("12345", 6))
        assertFalse(ValidationUtils.isValidPassword("abc", 6))
        assertFalse(ValidationUtils.isValidPassword("", 6))
    }
    
    @Test
    fun `contraseña vacía debe fallar validación`() {
        assertFalse(ValidationUtils.isValidPassword("", 6))
        assertFalse(ValidationUtils.isValidPassword("   ", 6))
    }
    
    @Test
    fun `contraseña con longitud mínima personalizada`() {
        assertTrue(ValidationUtils.isValidPassword("1234", 4))
        assertFalse(ValidationUtils.isValidPassword("123", 4))
    }
    
    // ========== Tests de validación de Nombre ==========
    
    @Test
    fun `nombre válido debe pasar validación`() {
        assertTrue(ValidationUtils.isValidName("Juan"))
        assertTrue(ValidationUtils.isValidName("María José"))
        assertTrue(ValidationUtils.isValidName("José María"))
    }
    
    @Test
    fun `nombre corto debe fallar validación`() {
        assertFalse(ValidationUtils.isValidName("A", 2))
        assertFalse(ValidationUtils.isValidName("", 2))
    }
    
    @Test
    fun `nombre vacío debe fallar validación`() {
        assertFalse(ValidationUtils.isValidName("", 2))
        assertFalse(ValidationUtils.isValidName("   ", 2))
        assertFalse(ValidationUtils.isValidName("\t\n", 2))
    }
    
    @Test
    fun `nombre con espacios debe ser válido después de trim`() {
        assertTrue(ValidationUtils.isValidName("  Juan  ", 2))
    }
    
    // ========== Tests de mensajes de error ==========
    
    @Test
    fun `getEmailError retorna null para email válido`() {
        assertNull(ValidationUtils.getEmailError("usuario@ejemplo.com"))
    }
    
    @Test
    fun `getEmailError retorna mensaje para email vacío`() {
        val error = ValidationUtils.getEmailError("")
        assertNotNull(error)
        assertEquals("El email no puede estar vacío", error)
    }
    
    @Test
    fun `getEmailError retorna mensaje para email inválido`() {
        val error = ValidationUtils.getEmailError("email-invalido")
        assertNotNull(error)
        assertEquals("El formato del email no es válido", error)
    }
    
    @Test
    fun `getPasswordError retorna null para contraseña válida`() {
        assertNull(ValidationUtils.getPasswordError("password123", 6))
    }
    
    @Test
    fun `getPasswordError retorna mensaje para contraseña vacía`() {
        val error = ValidationUtils.getPasswordError("", 6)
        assertNotNull(error)
        assertEquals("La contraseña no puede estar vacía", error)
    }
    
    @Test
    fun `getPasswordError retorna mensaje para contraseña corta`() {
        val error = ValidationUtils.getPasswordError("12345", 6)
        assertNotNull(error)
        assertEquals("La contraseña debe tener al menos 6 caracteres", error)
    }
    
    @Test
    fun `getNameError retorna null para nombre válido`() {
        assertNull(ValidationUtils.getNameError("Juan", 2))
    }
    
    @Test
    fun `getNameError retorna mensaje para nombre vacío`() {
        val error = ValidationUtils.getNameError("", 2)
        assertNotNull(error)
        assertEquals("El nombre no puede estar vacío", error)
    }
    
    @Test
    fun `getNameError retorna mensaje para nombre corto`() {
        val error = ValidationUtils.getNameError("A", 2)
        assertNotNull(error)
        assertEquals("El nombre debe tener al menos 2 caracteres", error)
    }
}
