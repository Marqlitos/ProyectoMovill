package com.example.proyectom.data.repository

import com.example.proyectom.model.UserProfile

interface UserRepository {
    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        age: Int,
        phone: String
    ): Result<UserProfile>

    suspend fun login(email: String, password: String): Result<UserProfile>

    suspend fun getCurrentUser(): UserProfile?

    suspend fun updateProfileImage(userId: String, profileImagePath: String): Result<UserProfile>

    suspend fun logout()
}
