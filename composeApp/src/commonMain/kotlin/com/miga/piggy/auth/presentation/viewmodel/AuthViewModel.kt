package com.miga.piggy.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.model.User
import com.miga.piggy.auth.domain.usecase.EmailVerificationUseCase
import com.miga.piggy.auth.domain.usecase.GetCurrentUserUseCase
import com.miga.piggy.auth.domain.usecase.LoginUseCase
import com.miga.piggy.auth.domain.usecase.LogoutUseCase
import com.miga.piggy.auth.domain.usecase.RegisterUseCase
import com.miga.piggy.auth.presentation.state.AuthUiState
import com.miga.piggy.auth.presentation.state.LoginFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val emailVerificationUseCase: EmailVerificationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(LoginFormState())
    val formState: StateFlow<LoginFormState> = _formState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun login() {
        val currentForm = _formState.value
        if (!validateForm(currentForm, isLogin = true)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = loginUseCase(currentForm.email, currentForm.password)) {
                is AuthResult.Success -> {
                    try {
                        val isVerified = emailVerificationUseCase.isEmailVerified()
                        if (isVerified) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                user = result.data,
                                isEmailVerified = true,
                                error = null
                            )
                        } else {
                            logout()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                user = null,
                                isEmailVerified = false,
                                error = "Por favor, verifique seu e-mail antes de acessar."
                            )
                        }
                    } catch (e: Exception) {
                        if (isUserDeletedError(e)) {
                            logout()
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                user = null,
                                isEmailVerified = false,
                                error = "Conta não encontrada. Faça login novamente."
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
                }

                is AuthResult.Error -> {
                    val errorMessage = if (isUserDeletedError(result.exception)) {
                        "Conta não encontrada. Faça login novamente."
                    } else {
                        result.exception.message
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }

                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun register() {
        val currentForm = _formState.value
        if (!validateForm(currentForm, isLogin = false)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result =
                registerUseCase(
                    currentForm.email,
                    currentForm.password,
                    currentForm.displayName
                )
            ) {
                is AuthResult.Success<User> -> {
                    emailVerificationUseCase.resendVerificationEmail()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message
                    )
                }

                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (logoutUseCase()) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(user = null)
                    clearForm()
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = "Erro ao fazer logout"
                    )
                }

                else -> {}
            }
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is AuthResult.Success -> {
                    if (result.data != null) {
                        try {
                            _uiState.value = _uiState.value.copy(user = result.data)
                        } catch (e: Exception) {
                            if (isUserDeletedError(e)) {
                                logout()
                                _uiState.value = _uiState.value.copy(
                                    user = null,
                                    error = "Conta não encontrada. Faça login novamente."
                                )
                            }
                        }
                    }
                }
                is AuthResult.Error -> {
                    if (isUserDeletedError(result.exception)) {
                        logout()
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            error = "Conta não encontrada. Faça login novamente."
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(email = email, emailError = null)
    }

    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(password = password, passwordError = null)
    }

    fun updateDisplayName(displayName: String) {
        _formState.value = _formState.value.copy(displayName = displayName, displayNameError = null)
    }

    fun checkEmailVerificationStatus() {
        viewModelScope.launch {
            val isVerified = emailVerificationUseCase.isEmailVerified()
            _uiState.value = _uiState.value.copy(isEmailVerified = isVerified)
        }
    }

    fun resendVerificationEmail() {
        viewModelScope.launch {
            val success = emailVerificationUseCase.resendVerificationEmail()
            if (success) {
                _uiState.value = _uiState.value.copy(verificationEmailSent = true)
            } else {
                _uiState.value = _uiState.value.copy(error = "Falha ao reenviar o e-mail.")
            }
        }
    }

    fun checkIfUserIsLoggedAndVerified() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is AuthResult.Success -> {
                    val user = result.data
                    if (user != null) {
                        try {
                            val isVerified = emailVerificationUseCase.isEmailVerified()
                            _uiState.value = _uiState.value.copy(
                                user = user,
                                isEmailVerified = isVerified
                            )
                        } catch (e: Exception) {
                            if (isUserDeletedError(e)) {
                                logout()
                                _uiState.value = _uiState.value.copy(
                                    user = null,
                                    isEmailVerified = false,
                                    error = "Conta não encontrada. Faça login novamente."
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    user = null,
                                    isEmailVerified = false,
                                    error = e.message
                                )
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            isEmailVerified = false
                        )
                    }
                }
                is AuthResult.Error -> {
                    if (isUserDeletedError(result.exception)) {
                        logout()
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            isEmailVerified = false,
                            error = "Conta não encontrada. Faça login novamente."
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            user = null,
                            isEmailVerified = false,
                            error = result.exception.message
                        )
                    }
                }
                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun isUserDeletedError(exception: Throwable): Boolean {
        val message = exception.message?.lowercase() ?: ""
        return message.contains("no user record") ||
                message.contains("user may have been deleted") ||
                message.contains("user not found") ||
                message.contains("invalid user")
    }

    private fun validateForm(form: LoginFormState, isLogin: Boolean): Boolean {
        var isValid = true
        var emailError: String? = null
        var passwordError: String? = null
        var displayNameError: String? = null

        if (form.email.isBlank()) {
            emailError = "Email é obrigatório"
            isValid = false
        }
        if (form.password.length < 6) {
            passwordError = "Senha deve ter pelo menos 6 caracteres"
            isValid = false
        }
        if (!isLogin && form.displayName.isBlank()) {
            displayNameError = "Nome é obrigatório"
            isValid = false
        }

        _formState.value = form.copy(
            emailError = emailError,
            passwordError = passwordError,
            displayNameError = displayNameError
        )

        return isValid
    }

    private fun clearForm() {
        _formState.value = LoginFormState()
    }
}