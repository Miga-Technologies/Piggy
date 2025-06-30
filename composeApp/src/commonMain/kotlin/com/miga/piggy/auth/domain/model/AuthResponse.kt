package com.miga.piggy.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val kind: String,
    val localId: String,
    val email: String,
    val displayName: String? = null,
    val idToken: String,
    val registered: Boolean,
    val refreshToken: String,
    val expiresIn: String,
    @SerialName("email_verified")
    val emailVerified: Boolean = false
)