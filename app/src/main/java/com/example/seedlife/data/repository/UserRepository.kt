package com.example.seedlife.data.repository

import com.example.seedlife.data.model.UserProfile
import com.example.seedlife.util.FirebaseErrorMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repositorio que maneja las operaciones de perfil de usuario
 */
class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Observa el perfil del usuario en tiempo real
     * @param uid ID del usuario
     * @return Flow con el UserProfile o null si no existe
     */
    fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val userDoc = firestore
            .collection("users")
            .document(uid)

        val listenerRegistration: ListenerRegistration = userDoc.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, error ->
            if (error != null) {
                close(Exception(FirebaseErrorMapper.mapException(error)))
                return@addSnapshotListener
            }

            val profile = snapshot?.toObject(UserProfile::class.java)
            trySend(profile)
        }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Cierra la sesión actual
     */
    fun signOut() {
        auth.signOut()
    }
}
