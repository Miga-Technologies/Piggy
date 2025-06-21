package com.miga.piggy.auth.presentation.state

import com.miga.piggy.auth.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoginMode: Boolean = true,
    val isEmailVerified: Boolean? = null,
    val verificationEmailSent: Boolean = false
)