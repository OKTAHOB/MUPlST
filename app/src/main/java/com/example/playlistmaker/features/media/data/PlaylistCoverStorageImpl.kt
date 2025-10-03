package com.example.playlistmaker.features.media.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.example.playlistmaker.features.media.domain.repository.PlaylistCoverStorage
import java.io.File
import java.io.FileOutputStream

class PlaylistCoverStorageImpl(
    private val context: Context
) : PlaylistCoverStorage {

    override suspend fun copyCoverToStorage(sourceUri: String, playlistName: String): String {
        val resolver = context.contentResolver
        val uri = Uri.parse(sourceUri)

        val extension = resolver.getType(uri)?.let { mimeType ->
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        } ?: "jpg"

        val coversDir = File(context.filesDir, COVERS_DIRECTORY)
        if (!coversDir.exists()) {
            coversDir.mkdirs()
        }

        val safeName = playlistName.filter { it.isLetterOrDigit() }
            .ifEmpty { DEFAULT_FILE_PREFIX }
        val fileName = "$safeName-${System.currentTimeMillis()}.$extension"
        val destination = File(coversDir, fileName)

        val inputStream = resolver.openInputStream(uri) ?: return ""
        inputStream.use { stream ->
            FileOutputStream(destination).use { outputStream ->
                stream.copyTo(outputStream)
            }
        }

        return destination.toUri().toString()
    }

    private companion object {
        const val COVERS_DIRECTORY = "playlist_covers"
        const val DEFAULT_FILE_PREFIX = "playlist"
    }
}
