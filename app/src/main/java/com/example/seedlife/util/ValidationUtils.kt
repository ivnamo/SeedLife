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
     * @param minLength Longitud mínima requerida (por defecto 6)
     * @return true si la contraseña tiene al menos minLength caracteres
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }

    /**
     * Valida si un nombre cumple con los requisitos mínimos
     * @param name El nombre a validar
     * @param minLength Longitud mínima requerida (por defecto 2)
     * @return true si el nombre tiene al menos minLength caracteres después de trim
     */
    fun isValidName(name: String, minLength: Int = 2): Boolean {
        return name.trim().length >= minLength
    }

    /**
     * Obtiene el mensaje de error para un email, o null si es válido
     * @param email El email a validar
     * @return Mensaje de error o null si el email es válido
     */
    fun getEmailError(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "El email no puede estar vacío"
            !isValidEmail(email) -> "El formato del email no es válido"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para una contraseña, o null si es válida
     * @param password La contraseña a validar
     * @param minLength Longitud mínima requerida (por defecto 6)
     * @return Mensaje de error o null si la contraseña es válida
     */
    fun getPasswordError(password: String, minLength: Int = 6): String? {
        return when {
            password.isEmpty() -> "La contraseña no puede estar vacía"
            password.length < minLength -> "La contraseña debe tener al menos $minLength caracteres"
            else -> null
        }
    }

    /**
     * Obtiene el mensaje de error para un nombre, o null si es válido
     * @param name El nombre a validar
     * @param minLength Longitud mínima requerida (por defecto 2)
     * @return Mensaje de error o null si el nombre es válido
     */
    fun getNameError(name: String, minLength: Int = 2): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "El nombre no puede estar vacío"
            trimmed.length < minLength -> "El nombre debe tener al menos $minLength caracteres"
            else -> null
        }
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
