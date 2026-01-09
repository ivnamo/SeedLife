package com.example.seedlife.data.repository

import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.example.seedlife.util.FirebaseErrorMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.io.File
import org.json.JSONObject

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

        val listenerRegistration: ListenerRegistration = wateringsCollection.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            if (error != null) {
                close(Exception(FirebaseErrorMapper.mapException(error)))
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

        val listenerRegistration: ListenerRegistration = seedDoc.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            if (error != null) {
                close(Exception(FirebaseErrorMapper.mapException(error)))
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
            Result.failure(Exception(FirebaseErrorMapper.mapException(e)))
        }
    }

    /**
     * Observa todas las seeds de un usuario en tiempo real
     * @param uid ID del usuario
     * @return Flow con la lista de seeds
     */
    fun observeSeeds(uid: String): Flow<List<Seed>> = callbackFlow {
        // #region agent log
        try {
            val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
            val logEntry = JSONObject().apply {
                put("sessionId", "debug-session")
                put("runId", "run1")
                put("hypothesisId", "A")
                put("location", "SeedRepository.observeSeeds:152")
                put("message", "observeSeeds called")
                put("data", JSONObject().apply {
                    put("uid", uid)
                    put("path", "users/$uid/seeds")
                })
                put("timestamp", System.currentTimeMillis())
            }
            logFile.appendText(logEntry.toString() + "\n")
        } catch (e: Exception) {}
        // #endregion
        val seedsCollection = firestore
            .collection("users")
            .document(uid)
            .collection("seeds")

        val listenerRegistration: ListenerRegistration = seedsCollection.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            // #region agent log
            try {
                val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
                val logEntry = JSONObject().apply {
                    put("sessionId", "debug-session")
                    put("runId", "run1")
                    put("hypothesisId", "A")
                    put("location", "SeedRepository.observeSeeds:snapshot")
                    put("message", "snapshot received")
                    put("data", JSONObject().apply {
                        put("hasError", error != null)
                        put("error", error?.message ?: "null")
                        put("docCount", snapshot?.documents?.size ?: 0)
                        put("hasMetadata", snapshot?.metadata?.hasPendingWrites ?: false)
                    })
                    put("timestamp", System.currentTimeMillis())
                }
                logFile.appendText(logEntry.toString() + "\n")
            } catch (e: Exception) {}
            // #endregion
            if (error != null) {
                close(Exception(FirebaseErrorMapper.mapException(error)))
                return@addSnapshotListener
            }

            val seeds = snapshot?.documents?.mapNotNull { doc ->
                val seed = doc.toObject(Seed::class.java)
                // #region agent log
                try {
                    val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
                    val logEntry = JSONObject().apply {
                        put("sessionId", "debug-session")
                        put("runId", "run1")
                        put("hypothesisId", "B")
                        put("location", "SeedRepository.observeSeeds:map")
                        put("message", "mapping document")
                        put("data", JSONObject().apply {
                            put("docId", doc.id)
                            put("seedNotNull", seed != null)
                            put("seedTitle", seed?.title ?: "null")
                        })
                        put("timestamp", System.currentTimeMillis())
                    }
                    logFile.appendText(logEntry.toString() + "\n")
                } catch (e: Exception) {}
                // #endregion
                seed?.id = doc.id
                seed
            } ?: emptyList()

            // #region agent log
            try {
                val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
                val logEntry = JSONObject().apply {
                    put("sessionId", "debug-session")
                    put("runId", "run1")
                    put("hypothesisId", "E")
                    put("location", "SeedRepository.observeSeeds:trySend")
                    put("message", "sending seeds to flow")
                    put("data", JSONObject().apply {
                        put("seedsCount", seeds.size)
                        put("seedIds", seeds.map { it.id })
                    })
                    put("timestamp", System.currentTimeMillis())
                }
                logFile.appendText(logEntry.toString() + "\n")
            } catch (e: Exception) {}
            // #endregion
            trySend(seeds)
        }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Crea una nueva seed
     * @param uid ID del usuario
     * @param title Título de la seed
     * @param description Descripción de la seed
     * @return Result con el ID de la seed creada o error
     */
    suspend fun createSeed(
        uid: String,
        title: String,
        description: String
    ): Result<String> {
        // #region agent log
        try {
            val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
            val logEntry = JSONObject().apply {
                put("sessionId", "debug-session")
                put("runId", "run1")
                put("hypothesisId", "C")
                put("location", "SeedRepository.createSeed:185")
                put("message", "createSeed called")
                put("data", JSONObject().apply {
                    put("uid", uid)
                    put("title", title)
                    put("path", "users/$uid/seeds")
                })
                put("timestamp", System.currentTimeMillis())
            }
            logFile.appendText(logEntry.toString() + "\n")
        } catch (e: Exception) {}
        // #endregion
        return try {
            val seed = Seed(
                title = title.trim(),
                description = description.trim(),
                level = 1,
                createdAt = Date()
            )

            val seedRef = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document()

            seedRef.set(seed).await()
            // #region agent log
            try {
                val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
                val logEntry = JSONObject().apply {
                    put("sessionId", "debug-session")
                    put("runId", "run1")
                    put("hypothesisId", "C")
                    put("location", "SeedRepository.createSeed:afterSet")
                    put("message", "seed created in firestore")
                    put("data", JSONObject().apply {
                        put("seedId", seedRef.id)
                        put("uid", uid)
                        put("fullPath", "users/$uid/seeds/${seedRef.id}")
                    })
                    put("timestamp", System.currentTimeMillis())
                }
                logFile.appendText(logEntry.toString() + "\n")
            } catch (e: Exception) {}
            // #endregion
            Result.success(seedRef.id)
        } catch (e: Exception) {
            // #region agent log
            try {
                val logFile = File("c:\\Users\\34692\\Desktop\\repos\\SeedLife\\.cursor\\debug.log")
                val logEntry = JSONObject().apply {
                    put("sessionId", "debug-session")
                    put("runId", "run1")
                    put("hypothesisId", "C")
                    put("location", "SeedRepository.createSeed:error")
                    put("message", "createSeed failed")
                    put("data", JSONObject().apply {
                        put("error", e.message ?: "unknown")
                    })
                    put("timestamp", System.currentTimeMillis())
                }
                logFile.appendText(logEntry.toString() + "\n")
            } catch (ex: Exception) {}
            // #endregion
            Result.failure(e)
        }
    }

    /**
     * Actualiza una seed existente
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @param title Nuevo título
     * @param description Nueva descripción
     * @return Result con éxito o error
     */
    suspend fun updateSeed(
        uid: String,
        seedId: String,
        title: String,
        description: String
    ): Result<Unit> {
        return try {
            val seedRef = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)

            seedRef.update(
                mapOf(
                    "title" to title.trim(),
                    "description" to description.trim()
                )
            ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(FirebaseErrorMapper.mapException(e)))
        }
    }

    /**
     * Elimina una seed y todos sus waterings (borrado en cascada)
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @return Result con éxito o error
     */
    suspend fun deleteSeed(uid: String, seedId: String): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val wateringsCollection = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
                .collection("waterings")

            // Leer todos los waterings en lotes
            var hasMore = true
            while (hasMore) {
                val wateringsSnapshot = wateringsCollection.limit(500).get().await()
                
                if (wateringsSnapshot.isEmpty) {
                    hasMore = false
                } else {
                    wateringsSnapshot.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                    
                    // Si hay menos de 500, ya terminamos
                    if (wateringsSnapshot.size() < 500) {
                        hasMore = false
                    }
                }
            }

            // Borrar la seed
            val seedRef = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
            
            batch.delete(seedRef)

            // Ejecutar el batch
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(FirebaseErrorMapper.mapException(e)))
        }
    }
}
