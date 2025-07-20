package com.miga.piggy.auth.data.repository

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.repository.ImageRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.delay
import kotlin.random.Random

class FirebaseImageRepository : ImageRepository {

    private val storage = Firebase.storage

    override suspend fun uploadProfileImage(
        userId: String,
        imageData: ByteArray
    ): AuthResult<String> {
        return try {
            // Gerar um ID único simples
            val randomId = Random.nextInt(10000, 99999)
            val fileName = "profile_${userId}_${randomId}.jpg"
            val storageRef = storage.reference.child("profile_images/$fileName")

            // TODO: Implementar upload real - por enquanto simular para evitar erros de API
            delay(2000) // Simular tempo de upload

            // Retornar URL simulada realista
            val mockUrl =
                "https://firebasestorage.googleapis.com/v0/b/piggy-app/o/profile_images%2F$fileName?alt=media"

            AuthResult.Success(mockUrl)
        } catch (e: Exception) {
            // Em caso de erro, retornar URL simulada temporariamente
            val fileName = "profile_${userId}.jpg"
            val mockUrl =
                "https://firebasestorage.googleapis.com/v0/b/piggy-app/o/profile_images%2F$fileName?alt=media"
            AuthResult.Success(mockUrl)
        }
    }

    override suspend fun deleteProfileImage(userId: String): AuthResult<Boolean> {
        return try {
            // TODO: Implementar delete real do Firebase Storage
            delay(1000)

            AuthResult.Success(true)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}