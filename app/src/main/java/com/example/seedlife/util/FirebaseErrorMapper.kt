package com.example.seedlife.util

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Mapea excepciones de Firebase a mensajes de error amigables para el usuario
 */
object FirebaseErrorMapper {
    /**
     * Mapea una excepción a un mensaje de error en español
     */
    fun mapException(e: Exception): String {
        return when (e) {
            is FirebaseFirestoreException -> mapFirestoreException(e)
            is FirebaseAuthException -> mapAuthException(e)
            else -> e.message ?: "Error desconocido"
        }
    }

    /**
     * Mapea excepciones de Firestore
     */
    private fun mapFirestoreException(e: FirebaseFirestoreException): String {
        return when (e.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> 
                "No tienes permiso para realizar esta acción"
            FirebaseFirestoreException.Code.UNAVAILABLE -> 
                "Error de conexión. Verifica tu conexión a internet"
            FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> 
                "La operación tardó demasiado. Intenta de nuevo"
            FirebaseFirestoreException.Code.NOT_FOUND -> 
                "No se encontró el recurso solicitado"
            FirebaseFirestoreException.Code.ALREADY_EXISTS -> 
                "El recurso ya existe"
            FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> 
                "Se ha excedido la cuota. Intenta más tarde"
            FirebaseFirestoreException.Code.FAILED_PRECONDITION -> 
                "La operación no se puede completar en este momento"
            FirebaseFirestoreException.Code.ABORTED -> 
                "La operación fue cancelada"
            FirebaseFirestoreException.Code.OUT_OF_RANGE -> 
                "Valor fuera de rango"
            FirebaseFirestoreException.Code.UNIMPLEMENTED -> 
                "Operación no implementada"
            FirebaseFirestoreException.Code.INTERNAL -> 
                "Error interno del servidor"
            FirebaseFirestoreException.Code.UNKNOWN -> 
                "Error desconocido de Firestore"
            FirebaseFirestoreException.Code.INVALID_ARGUMENT -> 
                "Argumentos inválidos"
            FirebaseFirestoreException.Code.CANCELLED -> 
                "Operación cancelada"
            else -> "Error de Firestore: ${e.message ?: e.code.name}"
        }
    }

    /**
     * Mapea excepciones de Firebase Auth
     */
    private fun mapAuthException(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "El formato del email no es válido"
            "ERROR_WEAK_PASSWORD" -> "La contraseña es muy débil. Debe tener al menos 6 caracteres"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este email ya está registrado"
            "ERROR_USER_NOT_FOUND" -> "No existe una cuenta con este email"
            "ERROR_WRONG_PASSWORD" -> "Contraseña incorrecta"
            "ERROR_USER_DISABLED" -> "Esta cuenta ha sido deshabilitada"
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Intenta más tarde"
            "ERROR_OPERATION_NOT_ALLOWED" -> "Esta operación no está permitida"
            "ERROR_INVALID_CREDENTIAL" -> "Credenciales inválidas"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Error de conexión. Verifica tu internet"
            else -> "Error de autenticación: ${e.message ?: e.errorCode}"
        }
    }
}
