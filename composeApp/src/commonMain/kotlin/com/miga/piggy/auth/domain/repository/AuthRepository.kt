package com.miga.piggy.auth.domain.repository

import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<User>
    suspend fun register(email: String, password: String, displayName: String): AuthResult<User>
    suspend fun getCurrentUser(): AuthResult<User?>
    suspend fun logout(): AuthResult<Unit>
    suspend fun resetPassword(email: String): AuthResult<Unit>
    suspend fun sendEmailVerification(): Boolean
    suspend fun isEmailVerified(): Boolean
    suspend fun updateUserProfile(displayName: String?, photoUrl: String?): AuthResult<Unit>
}