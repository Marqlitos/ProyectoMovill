package com.example.proyectom.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectom.ui.components.ImagePickerSection
import com.example.proyectom.viewmodel.AuthViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val user = uiState.currentUser

    LaunchedEffect(uiState.isAuthenticated) {
        if (!uiState.isAuthenticated) {
            onLogoutSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de usuario") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (user == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No has iniciado sesión",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inicia sesión o crea una cuenta para gestionar tu perfil.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar Sesión")
                }
            }
            return@Scaffold
        }

        val profileBitmap = remember(user.profileImagePath) {
            user.profileImagePath
                ?.takeIf { File(it).exists() }
                ?.let { BitmapFactory.decodeFile(it) }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterHorizontally),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "Sin foto de perfil",
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ImagePickerSection(
                        title = "Cambiar foto de perfil",
                        prefix = "perfil"
                    ) { selectedPath ->
                        authViewModel.updateProfileImage(selectedPath)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRow(label = "Nombre", value = user.fullName)
                    ProfileRow(label = "Correo", value = user.email)
                    ProfileRow(label = "Edad", value = "${user.age}")
                    ProfileRow(label = "Teléfono", value = user.phone)
                }
            }

            OutlinedButton(
                onClick = { authViewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, color = MaterialTheme.colorScheme.onSurface)
    }
}
