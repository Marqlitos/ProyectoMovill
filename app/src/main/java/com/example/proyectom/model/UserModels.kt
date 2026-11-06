package com.example.proyectom.model

/**
 * Modelo de usuario autenticado en la app.
 */
data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val age: Int = 0,
    val phone: String = "",
    val profileImagePath: String? = null
)

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val age: String,
    val phone: String
)
