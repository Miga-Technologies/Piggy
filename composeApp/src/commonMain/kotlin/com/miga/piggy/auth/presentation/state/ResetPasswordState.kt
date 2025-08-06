package com.miga.piggy.auth.presentation.state

/**
 * UI state for the reset password screen
 */
data class ResetPasswordUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailSent: Boolean = false
)

/**
 * Form state for the reset password screen
 */
data class ResetPasswordFormState(
    val email: String = "",
    val emailError: String? = null
)