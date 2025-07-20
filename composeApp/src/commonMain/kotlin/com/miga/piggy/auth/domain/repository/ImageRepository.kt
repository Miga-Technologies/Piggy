package com.miga.piggy.auth.domain.repository

import com.miga.piggy.auth.domain.model.AuthResult

interface ImageRepository {
    suspend fun uploadProfileImage(userId: String, imageData: ByteArray): AuthResult<String>
    suspend fun deleteProfileImage(userId: String): AuthResult<Boolean>
}