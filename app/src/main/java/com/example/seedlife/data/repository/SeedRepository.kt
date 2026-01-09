package com.example.seedlife.data.repository

import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repositorio que maneja las operaciones de Seeds y Waterings en Firestore
 */
class SeedRepository {
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observa los riegos de una seed en tiempo real
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @return Flow con la lista de waterings ordenados por date descendente
     */
    fun observeWaterings(uid: String, seedId: String): Flow<List<Watering>> = callbackFlow {
        val wateringsCollection = firestore
            .collection("users")
            .document(uid)
            .collection("seeds")
            .document(seedId)
            .collection("waterings")
            .orderBy("date", Query.Direction.DESCENDING)

        val listenerRegistration: ListenerRegistration = wateringsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val waterings = snapshot?.documents?.mapNotNull { doc ->
                val watering = doc.toObject(Watering::class.java)
                watering?.id = doc.id
                watering
            } ?: emptyList()

            trySend(waterings)
        }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Observa una seed en tiempo real
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @return Flow con la seed o null si no existe
     */
    fun observeSeed(uid: String, seedId: String): Flow<Seed?> = callbackFlow {
        val seedDoc = firestore
            .collection("users")
            .document(uid)
            .collection("seeds")
            .document(seedId)

        val listenerRegistration: ListenerRegistration = seedDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }

            val seed = snapshot?.toObject(Seed::class.java)
            seed?.id = snapshot?.id ?: seedId
            trySend(seed)
        }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Añade un nuevo riego y actualiza el level de la seed
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @param mood Estado de ánimo del riego
     * @param note Nota opcional
     * @return Result con éxito o error
     */
    suspend fun addWatering(
        uid: String,
        seedId: String,
        mood: WateringMood,
        note: String?
    ): Result<Unit> {
        return try {
            val wateringsCollection = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
                .collection("waterings")

            // Crear el documento de watering
            val watering = Watering(
                mood = mood.name,
                note = note,
                date = Date(),
                createdAt = Date()
            )

            // Añadir el watering
            val wateringRef = wateringsCollection.add(watering).await()

            // Contar total de waterings para calcular el level
            val wateringsSnapshot = wateringsCollection.get().await()
            val totalWaterings = wateringsSnapshot.size()

            // Calcular nuevo level: 1 + floor(totalWaterings / 3), cap a 5
            val newLevel = minOf(5, 1 + (totalWaterings / 3))

            // Actualizar la seed con el nuevo level y lastWateredAt
            val seedRef = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)

            seedRef.update(
                mapOf(
                    "level" to newLevel,
                    "lastWateredAt" to Date()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
