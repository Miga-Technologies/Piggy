package com.miga.piggy.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val message: String? = null
)