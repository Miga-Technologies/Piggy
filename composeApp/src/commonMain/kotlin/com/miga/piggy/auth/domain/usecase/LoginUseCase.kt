package com.miga.piggy.auth.domain.usecase

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.model.User
import com.miga.piggy.auth.domain.repository.AuthRepository

interface LoginUseCase {
    suspend operator fun invoke(email: String, password: String): AuthResult<User>
}

class LoginUseCaseImpl(
    private val authRepository: AuthRepository
) : LoginUseCase {
    override suspend fun invoke(email: String, password: String): AuthResult<User> {
        return try {
            if (!isValidEmail(email)) {
                return AuthResult.Error(IllegalArgumentException("Email inválido"))
            }
            if (password.length < 6) {
                return AuthResult.Error(IllegalArgumentException("Senha deve ter pelo menos 6 caracteres"))
            }
            authRepository.login(email, password)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return email.matches(emailRegex)
    }
}