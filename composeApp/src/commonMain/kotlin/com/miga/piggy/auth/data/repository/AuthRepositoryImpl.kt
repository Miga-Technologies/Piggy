package com.miga.piggy.auth.data.repository

import com.miga.piggy.auth.data.datasource.AuthDataSource
import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.model.User
import com.miga.piggy.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<User> {
        return try {
            val user = authDataSource.login(email, password)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): AuthResult<User> {
        return try {
            val user = authDataSource.register(email, password, displayName)
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun getCurrentUser(): AuthResult<User?> {
        return try {
            val user = authDataSource.getCurrentUser()
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            authDataSource.logout()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun resetPassword(email: String): AuthResult<Unit> {
        return try {
            authDataSource.resetPassword(email)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return authDataSource.isEmailVerified()
    }

    override suspend fun sendEmailVerification(): Boolean {
        return authDataSource.sendEmailVerification()
    }

    override suspend fun updateUserProfile(
        displayName: String?,
        photoUrl: String?
    ): AuthResult<Unit> {
        return try {
            authDataSource.updateUserProfile(displayName, photoUrl)
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e)
        }
    }
}