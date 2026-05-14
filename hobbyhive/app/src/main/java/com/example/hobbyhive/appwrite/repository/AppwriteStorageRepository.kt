package com.example.hobbyhive.appwrite.repository

import android.net.Uri
import com.example.hobbyhive.appwrite.AppwriteClient
import com.example.hobbyhive.appwrite.AppwriteConfig
import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.InputFile

// ═══════════════════════════════════════════════════════════════════════════
// AppwriteStorageRepository.kt
//
// Handles file upload, retrieval, and deletion using Appwrite Storage.
//
// Bucket: AppwriteConfig.BUCKET_HOBBY_IMAGES
//   → Used for both profile avatars and hobby banner images.
//
// How it works:
//   1. uploadImage()  — takes a local file Uri, uploads to Appwrite, returns fileId
//   2. getImageUrl()  — builds a preview URL from fileId (use with Coil in Compose)
//   3. deleteImage()  — removes the file from the Appwrite bucket
//
// In your Appwrite Console → Storage → bucket permissions, set:
//   • Read:  role:all   (anyone can view images)
//   • Write: users      (only logged-in users can upload/delete)
// ═══════════════════════════════════════════════════════════════════════════

class AppwriteStorageRepository {

    private val storage get() = AppwriteClient.storage

    /**
     * Upload a file from [fileUri] to the Appwrite Storage bucket.
     *
     * @param fileUri  Content URI of the file (from image picker or camera)
     * @param fileName A descriptive name to store in Appwrite (e.g. "avatar_userId.jpg")
     * @return         Result containing the Appwrite file ID on success
     *
     * Usage in ViewModel:
     *   val result = storageRepository.uploadImage(uri, "hobby_${hobbyId}.jpg")
     *   result.onSuccess { fileId -> hobby = hobby.copy(imageUri = fileId) }
     */
    suspend fun uploadImage(fileUri: Uri, fileName: String): Result<String> {
        return try {
            val context = AppwriteClient.context
            
            // Convert Android Uri to Appwrite InputFile via a temporary file
            val tempFile = java.io.File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val inputFile = InputFile.fromFile(tempFile)

            val file = storage.createFile(
                bucketId = AppwriteConfig.BUCKET_HOBBY_IMAGES,
                fileId   = ID.unique(),   // Appwrite generates a unique ID
                file     = inputFile
            )
            
            // Clean up temp file
            if (tempFile.exists()) tempFile.delete()
            
            Result.success(file.id)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Upload failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Build a public preview URL for a stored file.
     * This URL can be passed directly to Coil's AsyncImage composable.
     *
     * @param fileId  The Appwrite file ID returned by uploadImage()
     * @param width   Preview width in pixels (default 400 — good for thumbnails)
     * @param height  Preview height in pixels (default 400)
     * @return        A URL string ready for Coil / any image loader
     *
     * Example Compose usage:
     *   AsyncImage(model = storageRepository.getImageUrl(fileId), contentDescription = null)
     */
    fun getImageUrl(fileId: String, width: Int = 400, height: Int = 400): String {
        // Appwrite preview endpoint format:
        // {endpoint}/storage/buckets/{bucketId}/files/{fileId}/preview
        return "${AppwriteConfig.ENDPOINT}/storage/buckets/${AppwriteConfig.BUCKET_HOBBY_IMAGES}" +
               "/files/$fileId/preview" +
               "?width=$width&height=$height" +
               "&project=${AppwriteConfig.PROJECT_ID}"
    }

    /**
     * Build the direct download/view URL (full resolution, no resize).
     * Use this when you need the original file (e.g. sharing).
     */
    fun getImageViewUrl(fileId: String): String {
        return "${AppwriteConfig.ENDPOINT}/storage/buckets/${AppwriteConfig.BUCKET_HOBBY_IMAGES}" +
               "/files/$fileId/view" +
               "?project=${AppwriteConfig.PROJECT_ID}"
    }

    /**
     * Delete a file from Appwrite Storage.
     * Call this when a user removes their profile picture or deletes a hobby
     * that has an attached image.
     *
     * @param fileId  The Appwrite file ID to delete
     */
    suspend fun deleteImage(fileId: String): Result<Unit> {
        return try {
            storage.deleteFile(
                bucketId = AppwriteConfig.BUCKET_HOBBY_IMAGES,
                fileId   = fileId
            )
            Result.success(Unit)
        } catch (e: AppwriteException) {
            Result.failure(Exception(e.message ?: "Delete failed"))
        }
    }
}
