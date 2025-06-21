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
                    _uiState.value = _uiState.value.copy(user = result.data)
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
                    val isVerified = if (user != null) emailVerificationUseCase.isEmailVerified() else false
                    _uiState.value = _uiState.value.copy(
                        user = user,
                        isEmailVerified = isVerified
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        user = null,
                        isEmailVerified = false,
                        error = result.exception.message
                    )
                }
                is AuthResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
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