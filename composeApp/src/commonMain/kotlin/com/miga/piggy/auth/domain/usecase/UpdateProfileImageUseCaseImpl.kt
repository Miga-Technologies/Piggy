package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.repository.AuthRepository
import com.miga.piggy.auth.domain.repository.ImageRepository

class UpdateProfileImageUseCaseImpl(
    private val imageRepository: ImageRepository,
    private val authRepository: AuthRepository
) : UpdateProfileImageUseCase {

    override suspend fun invoke(userId: String, imageData: ByteArray): AuthResult<String> {
        return when (val uploadResult = imageRepository.uploadProfileImage(userId, imageData)) {
            is AuthResult.Success -> {
                when (val updateResult = authRepository.updateUserProfile(
                    displayName = null,
                    photoUrl = uploadResult.data
                )) {
                    is AuthResult.Success -> AuthResult.Success(uploadResult.data)
                    is AuthResult.Error -> AuthResult.Error(updateResult.exception)
                    is AuthResult.Loading -> AuthResult.Loading
                }
            }
            is AuthResult.Error -> AuthResult.Error(uploadResult.exception)
            is AuthResult.Loading -> AuthResult.Loading
        }
    }
}