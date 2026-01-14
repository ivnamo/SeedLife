package com.example.seedlife.data.util

import com.example.seedlife.data.model.Watering
import com.example.seedlife.data.model.WateringMood
import com.example.seedlife.data.repository.SeedRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

/**
 * Utilidad para crear datos mock en Firestore
 * Se ejecuta automáticamente una vez al detectar usuario autenticado
 */
object MockDataSeeder {
    private val seedRepository = SeedRepository()
    private val firestore = FirebaseFirestore.getInstance()
    
    /**
     * Crea seeds y waterings de ejemplo para un usuario
     * Solo se ejecuta si el usuario no tiene seeds existentes
     * @param uid ID del usuario autenticado
     */
    suspend fun seedMockData(uid: String) {
        try {
            // Verificar si ya hay seeds (evitar duplicados)
            val existingSeeds = seedRepository.observeSeeds(uid).first()
            if (existingSeeds.isNotEmpty()) {
                // Ya hay seeds, no crear datos mock
                return
            }
            
            // Datos de seeds mock
            val mockSeeds = listOf(
                MockSeedData(
                    title = "Tomate Cherry",
                    description = "Deliciosos tomates pequeños, perfectos para ensaladas",
                    waterings = listOf(
                        MockWateringData(WateringMood.GOOD, "Primera siembra exitosa", daysAgo = 7),
                        MockWateringData(WateringMood.OK, "Creciendo bien", daysAgo = 5),
                        MockWateringData(WateringMood.GOOD, "Primeras hojas apareciendo", daysAgo = 3)
                    )
                ),
                MockSeedData(
                    title = "Albahaca",
                    description = "Hierba aromática ideal para cocinar",
                    waterings = listOf(
                        MockWateringData(WateringMood.GOOD, "Germinación perfecta", daysAgo = 10),
                        MockWateringData(WateringMood.GOOD, "Creciendo rápido", daysAgo = 8),
                        MockWateringData(WateringMood.OK, "Necesita más sol", daysAgo = 6),
                        MockWateringData(WateringMood.GOOD, "Hojas grandes y verdes", daysAgo = 4),
                        MockWateringData(WateringMood.GOOD, "Lista para cosechar", daysAgo = 2)
                    )
                ),
                MockSeedData(
                    title = "Zanahoria",
                    description = "Hortaliza de raíz, rica en vitamina A",
                    waterings = listOf(
                        MockWateringData(WateringMood.OK, "Siembra inicial", daysAgo = 14),
                        MockWateringData(WateringMood.BAD, "Tierra muy seca", daysAgo = 12),
                        MockWateringData(WateringMood.OK, "Recuperándose", daysAgo = 10),
                        MockWateringData(WateringMood.GOOD, "Brotando", daysAgo = 7),
                        MockWateringData(WateringMood.GOOD, "Hojas creciendo", daysAgo = 5),
                        MockWateringData(WateringMood.GOOD, "Desarrollo normal", daysAgo = 3),
                        MockWateringData(WateringMood.GOOD, "Raíces formándose", daysAgo = 1)
                    )
                ),
                MockSeedData(
                    title = "Lechuga",
                    description = "Verde fresca para ensaladas",
                    waterings = listOf(
                        MockWateringData(WateringMood.GOOD, "Plantación exitosa", daysAgo = 6),
                        MockWateringData(WateringMood.GOOD, "Hojas pequeñas apareciendo", daysAgo = 4)
                    )
                ),
                MockSeedData(
                    title = "Pimiento",
                    description = "Pimientos rojos y verdes, picantes y dulces",
                    waterings = listOf(
                        MockWateringData(WateringMood.GOOD, "Semillas plantadas", daysAgo = 12),
                        MockWateringData(WateringMood.GOOD, "Primeros brotes", daysAgo = 10),
                        MockWateringData(WateringMood.OK, "Crecimiento lento", daysAgo = 8),
                        MockWateringData(WateringMood.GOOD, "Hojas desarrollándose", daysAgo = 6),
                        MockWateringData(WateringMood.GOOD, "Tallo fuerte", daysAgo = 4),
                        MockWateringData(WateringMood.GOOD, "Flores apareciendo", daysAgo = 2),
                        MockWateringData(WateringMood.GOOD, "Primeros frutos", daysAgo = 1),
                        MockWateringData(WateringMood.GOOD, "Cosecha próxima", daysAgo = 0)
                    )
                )
            )
            
            // Crear cada seed y sus waterings
            for (mockSeed in mockSeeds) {
                // Crear la seed usando el repositorio
                val seedIdResult = seedRepository.createSeed(uid, mockSeed.title, mockSeed.description)
                
                seedIdResult.getOrNull()?.let { seedId ->
                    // Crear waterings con fechas personalizadas
                    for (watering in mockSeed.waterings) {
                        val wateringDate = getDateDaysAgo(watering.daysAgo)
                        createWateringWithDate(uid, seedId, watering.mood, watering.note, wateringDate)
                        delay(50) // Pequeño delay entre waterings para evitar sobrecarga
                    }
                    
                    // Recalcular y actualizar el level de la seed después de crear todos los waterings
                    updateSeedLevel(uid, seedId)
                    
                    delay(100) // Delay entre seeds
                }
            }
        } catch (e: Exception) {
            // Silenciar errores para no interrumpir el flujo normal de la app
            // En producción podrías loguear esto con un sistema de logging
        }
    }
    
    /**
     * Crea un watering con una fecha específica directamente en Firestore
     */
    private suspend fun createWateringWithDate(
        uid: String,
        seedId: String,
        mood: WateringMood,
        note: String?,
        date: Date
    ) {
        try {
            val wateringsCollection = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
                .collection("waterings")
            
            // Crear el watering con fecha personalizada
            // Nota: Usamos un mapa en lugar del objeto Watering para evitar problemas con @ServerTimestamp
            val wateringData = hashMapOf<String, Any>(
                "mood" to mood.name,
                "date" to date,
                "createdAt" to date
            )
            
            if (note != null) {
                wateringData["note"] = note
            }
            
            wateringsCollection.add(wateringData).await()
        } catch (e: Exception) {
            // Manejar error silenciosamente
        }
    }
    
    /**
     * Actualiza el level de una seed basado en el número total de waterings
     */
    private suspend fun updateSeedLevel(uid: String, seedId: String) {
        try {
            val wateringsCollection = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
                .collection("waterings")
            
            // Contar total de waterings
            val wateringsSnapshot = wateringsCollection.get().await()
            val totalWaterings = wateringsSnapshot.size()
            
            // Calcular nuevo level: 1 + floor(totalWaterings / 3), cap a 5
            val newLevel = minOf(5, 1 + (totalWaterings / 3))
            
            // Obtener la fecha del último watering
            val lastWateringDate = if (wateringsSnapshot.documents.isNotEmpty()) {
                // Ordenar por fecha descendente y tomar el más reciente
                wateringsSnapshot.documents
                    .mapNotNull { it.getDate("date") }
                    .maxOrNull() ?: Date()
            } else {
                Date()
            }
            
            // Actualizar la seed con el nuevo level y lastWateredAt
            val seedRef = firestore
                .collection("users")
                .document(uid)
                .collection("seeds")
                .document(seedId)
            
            seedRef.update(
                mapOf(
                    "level" to newLevel,
                    "lastWateredAt" to lastWateringDate
                )
            ).await()
        } catch (e: Exception) {
            // Manejar error silenciosamente
        }
    }
    
    /**
     * Obtiene una fecha hace N días
     */
    private fun getDateDaysAgo(daysAgo: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return calendar.time
    }
    
    /**
     * Datos mock para una seed
     */
    private data class MockSeedData(
        val title: String,
        val description: String,
        val waterings: List<MockWateringData>
    )
    
    /**
     * Datos mock para un watering
     */
    private data class MockWateringData(
        val mood: WateringMood,
        val note: String?,
        val daysAgo: Int
    )
}
