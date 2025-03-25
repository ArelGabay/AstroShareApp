package com.example.astroshare.data.remote.firebase

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.astroshare.data.remote.cloudinary.CloudinaryConfig
import com.example.astroshare.data.remote.cloudinary.CloudinaryRetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class CloudinaryStorageService {

    suspend fun uploadUserImage(userId: String, imageUri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                // Try to get the real file path from the URI
                var filePath = getRealPathFromUri(context, imageUri)
                // If that fails, copy the content to a temporary file
                if (filePath.isNullOrEmpty()) {
                    filePath = copyUriToTempFile(context, imageUri)
                }
                val file = File(filePath)
                if (!file.exists()) {
                    throw Exception("File does not exist at path: $filePath")
                }
                // Create multipart request
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val presetBody = CloudinaryConfig.UPLOAD_PRESET.toRequestBody("text/plain".toMediaTypeOrNull())
                // Call Cloudinary API using Retrofit
                val response = CloudinaryRetrofitClient.instance.uploadImage(
                    cloudName = CloudinaryConfig.CLOUD_NAME,
                    file = filePart,
                    uploadPreset = presetBody
                )
                response.secure_url
            } catch (e: Exception) {
                Log.e("CloudinaryStorage", "Upload failed", e)
                throw e
            }
        }
    }

    // Try to resolve the real file path from the content URI.
    private fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
        }
        return path
    }

    // Fallback: copy the content of the URI to a temporary file in the cache directory.
    private fun copyUriToTempFile(context: Context, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Unable to open input stream from URI")
        val tempFile = File.createTempFile("upload", ".tmp", context.cacheDir)
        FileOutputStream(tempFile).use { output ->
            inputStream.copyTo(output)
        }
        return tempFile.absolutePath
    }
}