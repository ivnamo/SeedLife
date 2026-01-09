package com.example.seedlife.util

/**
 * Utilidades para validación de datos
 */
object ValidationUtils {
    
    /**
     * Valida si un email tiene un formato válido
     * @param email El email a validar
     * @return true si el email es válido, false en caso contrario
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) return false
        
        // Patrón básico de validación de email
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(trimmedEmail).matches()
    }
    
    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     * @param password La contraseña a validar
     * @param minLength Longitud mínima requerida (por defecto 6)
     * @return true si la contraseña es válida, false en caso contrario
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        if (password.isBlank()) return false
        return password.length >= minLength
    }
    
    /**
     * Valida si un nombre es válido
     * @param name El nombre a validar
     * @param minLength Longitud mínima requerida (por defecto 2)
     * @return true si el nombre es válido, false en caso contrario
     */
    fun isValidName(name: String, minLength: Int = 2): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) return false
        return trimmedName.length >= minLength
    }
    
    /**
     * Obtiene un mensaje de error para un email inválido
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            !isValidEmail(email) -> "El formato del email no es válido"
            else -> null
        }
    }
    
    /**
     * Obtiene un mensaje de error para una contraseña inválida
     */
    fun getPasswordError(password: String, minLength: Int = 6): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < minLength -> "La contraseña debe tener al menos $minLength caracteres"
            else -> null
        }
    }
    
    /**
     * Obtiene un mensaje de error para un nombre inválido
     */
    fun getNameError(name: String, minLength: Int = 2): String? {
        val trimmedName = name.trim()
        return when {
            trimmedName.isBlank() -> "El nombre no puede estar vacío"
            trimmedName.length < minLength -> "El nombre debe tener al menos $minLength caracteres"
            else -> null
        }
    }
}
