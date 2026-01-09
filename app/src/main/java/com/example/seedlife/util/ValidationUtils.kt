package com.example.seedlife.util

/**
 * Resultado de una validación
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)

/**
 * Utilidades para validación de formularios
 */
object ValidationUtils {
    // Regex simple para validar email
    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    /**
     * Valida si un email tiene formato válido
     */
    fun isValidEmail(email: String): Boolean {
        return email.trim().isNotBlank() && EMAIL_REGEX.matches(email.trim())
    }

    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     * @param password La contraseña a validar
     * @return true si la contraseña tiene al menos 6 caracteres
     */
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida el título de una seed
     * @param title El título a validar
     * @return ValidationResult con el resultado de la validación
     */
    fun validateSeedTitle(title: String): ValidationResult {
        val trimmed = title.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult(
                isValid = false,
                errorMessage = "El título no puede estar vacío"
            )
            trimmed.length < 3 -> ValidationResult(
                isValid = false,
                errorMessage = "El título debe tener al menos 3 caracteres"
            )
            else -> ValidationResult(isValid = true, errorMessage = null)
        }
    }

    /**
     * Valida la descripción de una seed
     * @param description La descripción a validar (puede ser null)
     * @return ValidationResult con el resultado de la validación
     */
    fun validateSeedDescription(description: String?): ValidationResult {
        val desc = description?.trim() ?: ""
        return when {
            desc.length > 200 -> ValidationResult(
                isValid = false,
                errorMessage = "La descripción no puede tener más de 200 caracteres"
            )
            else -> ValidationResult(isValid = true, errorMessage = null)
        }
    }

    /**
     * Valida la nota de un riego
     * @param note La nota a validar (puede ser null)
     * @return ValidationResult con el resultado de la validación
     */
    fun validateWateringNote(note: String?): ValidationResult {
        val noteText = note?.trim() ?: ""
        return when {
            noteText.length > 250 -> ValidationResult(
                isValid = false,
                errorMessage = "La nota no puede tener más de 250 caracteres"
            )
            else -> ValidationResult(isValid = true, errorMessage = null)
        }
    }
}
