package com.example.seedlife.data

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

/**
 * Configuración de Firestore, incluyendo persistencia offline
 */
object FirestoreConfig {
    private var persistenceEnabled = false

    /**
     * Habilita la persistencia offline de Firestore
     * Debe llamarse ANTES de cualquier uso de Firestore
     * @param context Contexto de la aplicación
     */
    fun enablePersistence(context: Context) {
        if (persistenceEnabled) return

        try {
            // La persistencia está habilitada por defecto en versiones recientes de Firestore
            // No es necesario configurarla explícitamente
            // Solo configuramos otros ajustes si es necesario
            val settings = FirebaseFirestoreSettings.Builder()
                .build()
            
            FirebaseFirestore.getInstance().firestoreSettings = settings
            persistenceEnabled = true
        } catch (e: Exception) {
            // Si ya se ha usado Firestore antes, la persistencia no se puede habilitar
            // Esto es normal y no es un error crítico
        }
    }
}
