package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.model.User
import com.miga.piggy.auth.domain.repository.AuthRepository

interface GetCurrentUserUseCase {
    suspend operator fun invoke(): AuthResult<User?>
}

class GetCurrentUserUseCaseImpl(
    private val authRepository: AuthRepository
) : GetCurrentUserUseCase {
    override suspend fun invoke(): AuthResult<User?> {
        return authRepository.getCurrentUser()
    }
}