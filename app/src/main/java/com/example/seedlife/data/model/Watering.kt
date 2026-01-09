package com.example.seedlife.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Enum para el estado de ánimo del riego
 */
enum class WateringMood {
    GOOD, OK, BAD
}

/**
 * Modelo de datos para un Watering en Firestore
 * Se almacena en la subcolección /users/{uid}/seeds/{seedId}/waterings/{wateringId}
 */
data class Watering(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    
    @get:PropertyName("mood")
    @set:PropertyName("mood")
    var mood: String = WateringMood.OK.name,
    
    @get:PropertyName("note")
    @set:PropertyName("note")
    var note: String? = null,
    
    @get:PropertyName("date")
    @set:PropertyName("date")
    @ServerTimestamp
    var date: Date? = null,
    
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    @ServerTimestamp
    var createdAt: Date? = null
) {
    fun getMoodEnum(): WateringMood {
        return try {
            WateringMood.valueOf(mood)
        } catch (e: Exception) {
            WateringMood.OK
        }
    }
}
