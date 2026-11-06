package com.example.proyectom.data.repository

import com.example.proyectom.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository : UserRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    override suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        age: Int,
        phone: String
    ): Result<UserProfile> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("No se pudo obtener el ID del usuario creado.")

            val userProfile = UserProfile(
                id = uid,
                fullName = fullName,
                email = email,
                age = age,
                phone = phone,
                profileImagePath = null
            )

            database.child(uid).setValue(userProfile).await()
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("No se pudo obtener el ID del usuario.")

            val snapshot = database.child(uid).get().await()
            val user = snapshot.getValue(UserProfile::class.java)
                ?: UserProfile(id = uid, fullName = "", email = email, age = 0, phone = "")

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val snapshot = database.child(uid).get().await()
            snapshot.getValue(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfileImage(userId: String, profileImagePath: String): Result<UserProfile> {
        return try {
            database.child(userId).child("profileImagePath").setValue(profileImagePath).await()
            val snapshot = database.child(userId).get().await()
            val user = snapshot.getValue(UserProfile::class.java)
                ?: throw IllegalStateException("Usuario no encontrado.")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
