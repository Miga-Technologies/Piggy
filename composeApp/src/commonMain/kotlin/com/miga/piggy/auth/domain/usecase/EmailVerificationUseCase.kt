package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.repository.AuthRepository

interface EmailVerificationUseCase {
    suspend fun isEmailVerified(): Boolean
    suspend fun resendVerificationEmail(): Boolean
}

class EmailVerificationUseCaseImpl(
    private val authRepository: AuthRepository
) : EmailVerificationUseCase {

    override suspend fun isEmailVerified(): Boolean {
        return authRepository.isEmailVerified()
    }

    override suspend fun resendVerificationEmail(): Boolean {
        return authRepository.sendEmailVerification()
    }
}