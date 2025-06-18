package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.repository.AuthRepository

interface LogoutUseCase {
    suspend operator fun invoke(): AuthResult<Unit>
}

class LogoutUseCaseImpl(
    private val authRepository: AuthRepository
) : LogoutUseCase {
    override suspend fun invoke(): AuthResult<Unit> {
        return authRepository.logout()
    }
}