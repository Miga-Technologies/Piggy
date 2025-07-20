package com.miga.piggy.auth.data.datasource

import com.miga.piggy.auth.domain.model.User

interface AuthDataSource {
    suspend fun login(email: String, password: String): User
    suspend fun register(email: String, password: String, displayName: String): User
    suspend fun getCurrentUser(): User?
    suspend fun logout()
    suspend fun resetPassword(email: String)
    suspend fun isEmailVerified(): Boolean
    suspend fun sendEmailVerification(): Boolean
    suspend fun updateUserProfile(displayName: String?, photoUrl: String?)
}