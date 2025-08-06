package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.repository.AuthRepository

class ResetPasswordUseCase(
    private val authRepository: AuthRepository
) {

    /**
     * Sends password reset email to the provided email address
     */
    suspend operator fun invoke(email: String): AuthResult<Unit> {
        // Validate email format
        if (email.isBlank()) {
            return AuthResult.Error(IllegalArgumentException("Email não pode estar vazio"))
        }

        if (!isValidEmail(email)) {
            return AuthResult.Error(IllegalArgumentException("Email inválido"))
        }

        return authRepository.resetPassword(email)
    }

    /**
     * Simple email validation using regex
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}