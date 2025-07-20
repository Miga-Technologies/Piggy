package com.miga.piggy.auth.data.repository

import com.miga.piggy.utils.Base64Utils
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Serializable
data class UserProfileImage(
    val userId: String = "",
    val imageBase64: String = "",
    val updatedAt: Long = 0L
)

interface ProfileImageRepository {
    suspend fun saveProfileImage(userId: String, imageData: ByteArray): Result<String>
    suspend fun getProfileImage(userId: String): Result<String?>
    suspend fun deleteProfileImage(userId: String): Result<Unit>
    fun observeProfileImage(userId: String): Flow<String?>
}

class FirestoreProfileImageRepository : ProfileImageRepository {

    private val firestore = Firebase.firestore
    private val collection = firestore.collection("user_profile_images")

    private fun getCurrentTimestamp(): Long {
        // Implementação simples que funciona em ambas as plataformas
        return 0L // Por enquanto, pode ser melhorado depois
    }

    override suspend fun saveProfileImage(userId: String, imageData: ByteArray): Result<String> {
        return try {
            val base64Image = Base64Utils.encodeToString(imageData)
            val profileImage = UserProfileImage(
                userId = userId,
                imageBase64 = base64Image,
                updatedAt = getCurrentTimestamp()
            )

            collection.document(userId).set(profileImage)
            Result.success(base64Image)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProfileImage(userId: String): Result<String?> {
        return try {
            val document = collection.document(userId).get()
            if (document.exists) {
                val profileImage = document.data<UserProfileImage>()
                Result.success(profileImage?.imageBase64)
            } else {
                Result.success(null) // Documento não existe, retorna null
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfileImage(userId: String): Result<Unit> {
        return try {
            collection.document(userId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeProfileImage(userId: String): Flow<String?> {
        return collection.document(userId).snapshots.map { snapshot ->
            try {
                snapshot.data<UserProfileImage>()?.imageBase64
            } catch (e: Exception) {
                null
            }
        }
    }
}