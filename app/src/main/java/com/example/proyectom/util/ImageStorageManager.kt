package com.example.proyectom.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object ImageStorageManager {

    fun createTempCameraUri(context: Context): Uri {
        val cacheDir = File(context.cacheDir, "camera_temp").apply { mkdirs() }
        val tempFile = File(cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        if (!tempFile.exists()) tempFile.createNewFile()
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    fun persistCapturedImage(context: Context, sourceUri: Uri, prefix: String = "img"): String? {
        return copyUriToInternalStorage(context, sourceUri, prefix)
    }

    fun copyGalleryImageToInternalStorage(context: Context, sourceUri: Uri, prefix: String = "img"): String? {
        return copyUriToInternalStorage(context, sourceUri, prefix)
    }

    private fun copyUriToInternalStorage(context: Context, sourceUri: Uri, prefix: String): String? {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
        val targetDir = File(context.filesDir, "images").apply { mkdirs() }
        val targetFile = File(targetDir, "${prefix}_${System.currentTimeMillis()}.jpg")

        inputStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        return targetFile.absolutePath
    }
}
