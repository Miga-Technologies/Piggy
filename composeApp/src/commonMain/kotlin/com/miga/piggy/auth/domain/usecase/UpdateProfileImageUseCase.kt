package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult

interface UpdateProfileImageUseCase {
    suspend operator fun invoke(userId: String, imageData: ByteArray): AuthResult<String>
}