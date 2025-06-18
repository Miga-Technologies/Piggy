package com.miga.piggy.auth.presentation.state

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val displayNameError: String? = null
)