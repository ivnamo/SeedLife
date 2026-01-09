package com.example.seedlife.data.model

import com.google.firebase.firestore.PropertyName

/**
 * Modelo de datos para el usuario en Firestore
 * Se almacena en la colección /users/{uid}
 */
data class User(
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    
    @get:PropertyName("email")
    @set:PropertyName("email")
    var email: String = "",
    
    @get:PropertyName("createdAt")
    @set:PropertyName("createdAt")
    var createdAt: Long = System.currentTimeMillis()
)
