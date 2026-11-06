package com.example.proyectom.data.repository.placeholder

import com.example.proyectom.data.repository.UserRepository
import com.example.proyectom.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlaceholderUserRepository : UserRepository {

    private val mutex = Mutex()

    private val users = mutableMapOf<String, Pair<UserProfile, String>>()
    private var currentUserId: String? = null

    override suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        age: Int,
        phone: String
    ): Result<UserProfile> {
        delay(250)
        return mutex.withLock {
            if (users.values.any { it.first.email.equals(email, ignoreCase = true) }) {
                return@withLock Result.failure(IllegalArgumentException("El correo ya está registrado."))
            }

            val user = UserProfile(
                id = "USR-${System.currentTimeMillis().toString().takeLast(7)}",
                fullName = fullName,
                email = email,
                age = age,
                phone = phone,
                profileImagePath = null
            )

            users[user.id] = user to password
            currentUserId = user.id

            // TODO BD/Backend: Reemplazar este almacenamiento en memoria por la inserción real
            // 1) Inyectar aquí un cliente de red (API REST/Firebase/Room) desde el constructor.
            // 2) Persistir usuario (sin contraseña en texto plano) en la fuente de datos real.
            // 3) Guardar token/sesión en almacenamiento seguro y devolver el perfil recibido del servidor.
            Result.success(user)
        }
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        delay(250)
        return mutex.withLock {
            val found = users.values.firstOrNull {
                it.first.email.equals(email, ignoreCase = true) && it.second == password
            }

            if (found == null) {
                return@withLock Result.failure(IllegalArgumentException("Correo o contraseña incorrectos."))
            }

            currentUserId = found.first.id

            // TODO BD/Backend: Reemplazar esta validación local por autenticación real contra backend.
            // Punto de inyección sugerido: AuthRemoteDataSource o UserApi en el constructor.
            Result.success(found.first)
        }
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return mutex.withLock {
            currentUserId?.let { users[it]?.first }
        }
    }

    override suspend fun updateProfileImage(userId: String, profileImagePath: String): Result<UserProfile> {
        delay(150)
        return mutex.withLock {
            val userAndPassword = users[userId]
                ?: return@withLock Result.failure(IllegalArgumentException("Usuario no encontrado."))

            val updatedUser = userAndPassword.first.copy(profileImagePath = profileImagePath)
            users[userId] = updatedUser to userAndPassword.second

            // TODO BD/Backend: Persistir la ruta/URL de imagen en la base real y sincronizar con backend.
            // Aquí se deberá reemplazar 'profileImagePath' local por URL pública retornada por el servicio.
            Result.success(updatedUser)
        }
    }

    override suspend fun logout() {
        delay(100)
        currentUserId = null
        // TODO BD/Backend: Invalidar token/sesión real en backend y limpiar credenciales seguras locales.
    }
}
