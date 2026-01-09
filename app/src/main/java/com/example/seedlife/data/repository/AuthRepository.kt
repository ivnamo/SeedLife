package com.example.seedlife.data.repository

import com.example.seedlife.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que maneja la autenticación y operaciones de usuario en Firestore
 */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    /**
     * Registra un nuevo usuario con email y contraseña
     * @return Result con el UID del usuario si es exitoso, o un error
     */
    suspend fun register(email: String, password: String, name: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No se pudo obtener el UID del usuario"))
            
            // Crear documento del usuario en Firestore
            val user = User(name = name, email = email)
            usersCollection.document(uid).set(user).await()
            
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     * @return Result con el UID del usuario si es exitoso, o un error
     */
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No se pudo obtener el UID del usuario"))
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los datos del usuario desde Firestore
     * @return Result con el User si existe, o un error
     */
    suspend fun getUserData(uid: String): Result<User?> {
        return try {
            val document = usersCollection.document(uid).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión actual
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Obtiene el UID del usuario actualmente autenticado
     * @return UID si hay un usuario autenticado, null en caso contrario
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Verifica si hay un usuario autenticado
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
