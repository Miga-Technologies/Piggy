package com.miga.piggy.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiUser(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String? = null,
    val emailVerified: Boolean = false
)