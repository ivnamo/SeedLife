package com.example.seedlife.ui.common

/**
 * Estado genérico de la UI
 * @param T Tipo de datos en caso de éxito
 */
sealed class UiState<out T> {
    /**
     * Estado de carga
     */
    object Loading : UiState<Nothing>()

    /**
     * Estado de éxito con datos
     * @param data Los datos obtenidos
     */
    data class Success<T>(val data: T) : UiState<T>()

    /**
     * Estado de error
     * @param message Mensaje de error para mostrar al usuario
     * @param retry Función opcional para reintentar la operación
     */
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
}
