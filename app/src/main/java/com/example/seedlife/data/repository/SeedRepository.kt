package com.example.seedlife.data.repository

import android.net.Uri
import com.example.seedlife.data.model.Seed
import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.example.seedlife.util.FirebaseErrorMapper
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.FirebaseStorage
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
    // Usar el bucket correcto que coincide con google-services.json
    private val storage = FirebaseStorage.getInstance("gs://seedlife-3a4d8.firebasestorage.app")

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
        val seedsCollection = firestore
            .collection("users")
            .document(uid)
            .collection("seeds")

        val listenerRegistration: ListenerRegistration = seedsCollection.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            if (error != null) {
                close(Exception(FirebaseErrorMapper.mapException(error)))
                return@addSnapshotListener
            }

            val seeds = snapshot?.documents?.mapNotNull { doc ->
                val seed = doc.toObject(Seed::class.java)
                seed?.id = doc.id
                seed
            } ?: emptyList()

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
            Result.success(seedRef.id)
        } catch (e: Exception) {
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

    /**
     * Sube una foto de una seed a Firebase Storage y actualiza el photoUrl en Firestore
     * @param uid ID del usuario
     * @param seedId ID de la seed
     * @param uri Uri de la imagen a subir
     * @return Result con la URL de descarga o error
     */
    suspend fun uploadSeedPhoto(
        uid: String,
        seedId: String,
        uri: Uri
    ): Result<String> {
        return try {
            // Verificar que el URI es válido
            if (uri == Uri.EMPTY) {
                return Result.failure(Exception("URI de imagen inválido"))
            }

            // Crear referencia en Storage: users/{uid}/seeds/{seedId}/cover_{timestamp}.jpg
            val timestamp = System.currentTimeMillis()
            val storageRef = storage.reference
                .child("users")
                .child(uid)
                .child("seeds")
                .child(seedId)
                .child("cover_$timestamp.jpg")

            // Subir el archivo
            val uploadTask = storageRef.putFile(uri)
            val snapshot = uploadTask.await()

            // Verificar que la subida fue exitosa
            if (snapshot.task.isSuccessful) {
                // Obtener la URL de descarga
                val downloadUrl = storageRef.downloadUrl.await().toString()

                // Actualizar Firestore con el photoUrl
                val seedRef = firestore
                    .collection("users")
                    .document(uid)
                    .collection("seeds")
                    .document(seedId)

                seedRef.update("photoUrl", downloadUrl).await()

                Result.success(downloadUrl)
            } else {
                val error = snapshot.error ?: Exception("Error desconocido al subir la imagen")
                Result.failure(Exception(FirebaseErrorMapper.mapException(error as? Exception ?: Exception(error.message, error))))
            }
        } catch (e: Exception) {
            // Manejo específico de errores de Storage
            val errorMessage = when {
                e.message?.contains("404") == true || e.message?.contains("Not Found") == true -> {
                    "Firebase Storage no está configurado. Verifica que Storage esté habilitado en Firebase Console y que las reglas de seguridad estén configuradas."
                }
                e.message?.contains("403") == true || e.message?.contains("Permission denied") == true -> {
                    "No tienes permiso para subir archivos. Verifica las reglas de Storage."
                }
                e.message?.contains("network") == true || e.message?.contains("Network") == true -> {
                    "Error de conexión. Verifica tu conexión a internet."
                }
                else -> FirebaseErrorMapper.mapException(e)
            }
            Result.failure(Exception(errorMessage))
        }
    }
}
