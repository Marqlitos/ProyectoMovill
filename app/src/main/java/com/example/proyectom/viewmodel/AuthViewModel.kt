package com.example.proyectom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectom.data.repository.UserRepository
import com.example.proyectom.data.repository.placeholder.PlaceholderUserRepository
import com.example.proyectom.model.AuthUiState
import com.example.proyectom.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository = PlaceholderUserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUser()
            if (currentUser != null) {
                _uiState.update {
                    it.copy(
                        isAuthenticated = true,
                        currentUser = currentUser,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun register(request: RegisterRequest) {
        val validationError = validateRegisterData(request)
        if (validationError != null) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = validationError,
                    successMessage = null
                )
            }
            return
        }

        val ageValue = request.age.trim().toInt()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            val result = userRepository.registerUser(
                fullName = request.fullName.trim(),
                email = request.email.trim(),
                password = request.password,
                age = ageValue,
                phone = request.phone.trim()
            )

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user,
                        errorMessage = null,
                        successMessage = "Registro exitoso."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No fue posible completar el registro.",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        val validationError = validateLoginData(email, password)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError, successMessage = null) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
            val result = userRepository.login(email.trim(), password)

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user,
                        errorMessage = null,
                        successMessage = "Inicio de sesión exitoso."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo iniciar sesión.",
                        successMessage = null
                    )
                }
            }
        }
    }

    fun updateProfileImage(imagePath: String) {
        val user = _uiState.value.currentUser ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.updateProfileImage(user.id, imagePath)
            result.onSuccess { updatedUser ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = updatedUser,
                        errorMessage = null,
                        successMessage = "Foto de perfil actualizada correctamente."
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "No se pudo actualizar la foto de perfil."
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun validateLoginData(email: String, password: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return "Debe completar correo y contraseña para iniciar sesión."
        }
        if (!isValidEmail(email)) {
            return "El formato del correo electrónico no es válido."
        }
        if (password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return null
    }

    private fun validateRegisterData(request: RegisterRequest): String? {
        if (
            request.fullName.isBlank() ||
            request.email.isBlank() ||
            request.password.isBlank() ||
            request.confirmPassword.isBlank() ||
            request.age.isBlank() ||
            request.phone.isBlank()
        ) {
            return "Todos los campos son obligatorios para crear la cuenta."
        }

        if (!isValidEmail(request.email)) {
            return "El formato del correo electrónico no es válido."
        }

        if (request.password.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }

        if (request.password != request.confirmPassword) {
            return "La confirmación de contraseña no coincide."
        }

        val age = request.age.toIntOrNull()
            ?: return "La edad debe ser numérica."

        if (age !in 18..99) {
            return "La edad debe estar entre 18 y 99 años. Esta validación es obligatoria por tratarse de una app de venta en línea."
        }

        if (request.phone.trim().length < 8) {
            return "El teléfono debe tener al menos 8 dígitos."
        }

        return null
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return emailRegex.matches(email.trim())
    }
}
