package com.example.seedlife.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Modelo de datos para una Seed en Firestore
 * Se almacena en la colección /users/{uid}/seeds/{seedId}
 */
data class Seed(
    // El id no se guarda en Firestore, es el ID del documento
    var id: String = "",
    
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    
    @get:PropertyName("level")
    @set:PropertyName("level")
    var level: Int = 1,
    
    @get:PropertyName("lastWateredAt")
    @set:PropertyName("lastWateredAt")
    var lastWateredAt: Date? = null,
    
    @get:PropertyName("photoUrl")
    @set:PropertyName("photoUrl")
    var photoUrl: String? = null,
    
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    @ServerTimestamp
    var createdAt: Date? = null
)
