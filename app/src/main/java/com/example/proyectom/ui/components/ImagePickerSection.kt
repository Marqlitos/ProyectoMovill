package com.example.proyectom.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.proyectom.util.ImageStorageManager

@Composable
fun ImagePickerSection(
    modifier: Modifier = Modifier,
    title: String,
    prefix: String,
    onImageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var permissionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(permissionError) {
        permissionError?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    val galleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) return@rememberLauncherForActivityResult
        val localUri = cameraUri ?: return@rememberLauncherForActivityResult
        val savedPath = ImageStorageManager.persistCapturedImage(context, localUri, prefix)
        if (savedPath != null) {
            onImageSelected(savedPath)
            permissionError = null
        } else {
            permissionError = "No se pudo guardar la foto tomada. Intente de nuevo."
        }
    }

    val pickFromGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { selectedUri ->
        if (selectedUri == null) return@rememberLauncherForActivityResult
        val savedPath = ImageStorageManager.copyGalleryImageToInternalStorage(context, selectedUri, prefix)
        if (savedPath != null) {
            onImageSelected(savedPath)
            permissionError = null
        } else {
            permissionError = "No se pudo copiar la imagen de la galería."
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            permissionError = "Permiso de cámara denegado. Se requiere para tomar una foto del producto o perfil."
            return@rememberLauncherForActivityResult
        }
        val tempUri = ImageStorageManager.createTempCameraUri(context)
        cameraUri = tempUri
        takePictureLauncher.launch(tempUri)
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            permissionError = "Permiso para acceder a imágenes denegado. Se requiere para elegir desde galería."
            return@rememberLauncherForActivityResult
        }
        pickFromGalleryLauncher.launch("image/*")
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        val tempUri = ImageStorageManager.createTempCameraUri(context)
                        cameraUri = tempUri
                        takePictureLauncher.launch(tempUri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Tomar foto")
            }

            OutlinedButton(
                onClick = {
                    val granted = ContextCompat.checkSelfPermission(
                        context,
                        galleryPermission
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        pickFromGalleryLauncher.launch("image/*")
                    } else {
                        galleryPermissionLauncher.launch(galleryPermission)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Elegir de galería")
            }
        }

        permissionError?.let {
            Text(
                text = it,
                color = Color(0xFFB00020),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))
    }
}
