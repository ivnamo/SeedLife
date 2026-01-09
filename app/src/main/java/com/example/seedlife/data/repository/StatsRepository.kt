package com.example.seedlife.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Modelo de estadísticas del usuario
 */
data class UserStats(
    val totalSeeds: Int = 0,
    val totalWaterings: Int = 0
)

/**
 * Repositorio que maneja las estadísticas del usuario
 */
class StatsRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observa las estadísticas del usuario en tiempo real
     * @param uid ID del usuario
     * @return Flow con las estadísticas
     */
    fun observeStats(uid: String): Flow<UserStats> = callbackFlow {
        val statsDoc = firestore
            .collection("users")
            .document(uid)
            .collection("stats")
            .document("summary")

        val listenerRegistration: ListenerRegistration = statsDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(UserStats())
                return@addSnapshotListener
            }

            val totalSeeds = snapshot?.getLong("totalSeeds")?.toInt() ?: 0
            val totalWaterings = snapshot?.getLong("totalWaterings")?.toInt() ?: 0

            trySend(UserStats(totalSeeds = totalSeeds, totalWaterings = totalWaterings))
        }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Actualiza las estadísticas del usuario
     * @param uid ID del usuario
     * @param totalSeeds Total de seeds
     * @param totalWaterings Total de waterings
     */
    suspend fun updateStats(uid: String, totalSeeds: Int, totalWaterings: Int) {
        try {
            val statsDoc = firestore
                .collection("users")
                .document(uid)
                .collection("stats")
                .document("summary")

            statsDoc.set(
                mapOf(
                    "totalSeeds" to totalSeeds,
                    "totalWaterings" to totalWaterings
                )
            ).await()
        } catch (e: Exception) {
            // Error al actualizar stats, se puede ignorar o loguear
        }
    }

    /**
     * Calcula las estadísticas desde las colecciones reales
     * Útil para inicializar o recalcular
     */
    suspend fun calculateStats(uid: String): UserStats {
        return try {
            // Contar seeds
            val seedsSnapshot = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .get()
                .await()
            
            val totalSeeds = seedsSnapshot.size()

            // Contar waterings de todas las seeds
            var totalWaterings = 0
            for (seedDoc in seedsSnapshot.documents) {
                val wateringsSnapshot = firestore
                    .collection("users")
                    .document(uid)
                    .collection("seeds")
                    .document(seedDoc.id)
                    .collection("waterings")
                    .get()
                    .await()
                totalWaterings += wateringsSnapshot.size()
            }

            UserStats(totalSeeds = totalSeeds, totalWaterings = totalWaterings)
        } catch (e: Exception) {
            UserStats()
        }
    }
}
