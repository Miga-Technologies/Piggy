package com.miga.piggy.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.domain.model.AuthResult
import com.miga.piggy.auth.domain.usecase.ResetPasswordUseCase
import com.miga.piggy.auth.presentation.state.ResetPasswordFormState
import com.miga.piggy.auth.presentation.state.ResetPasswordUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(ResetPasswordFormState())
    val formState: StateFlow<ResetPasswordFormState> = _formState.asStateFlow()

    /**
     * Updates the email field in the form
     */
    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(
            email = email,
            emailError = null
        )
        // Clear any previous errors when user starts typing
        if (_uiState.value.error != null) {
            _uiState.value = _uiState.value.copy(error = null)
        }
    }

    /**
     * Validates email format
     */
    private fun validateEmail(): Boolean {
        val email = _formState.value.email
        return when {
            email.isBlank() -> {
                _formState.value = _formState.value.copy(
                    emailError = "Email é obrigatório"
                )
                false
            }

            !isValidEmail(email) -> {
                _formState.value = _formState.value.copy(
                    emailError = "Digite um email válido"
                )
                false
            }

            else -> {
                _formState.value = _formState.value.copy(emailError = null)
                true
            }
        }
    }

    /**
     * Sends password reset email
     */
    fun resetPassword() {
        if (!validateEmail()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            when (val result = resetPasswordUseCase(_formState.value.email)) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isEmailSent = true,
                        error = null
                    )
                }

                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = getErrorMessage(result.exception)
                    )
                }
                is AuthResult.Loading -> {
                    // Loading state is already handled above
                }
            }
        }
    }

    /**
     * Simple email validation using regex
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Converts exception to user-friendly error message
     */
    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("user-not-found", ignoreCase = true) == true ->
                "Email não encontrado"

            exception.message?.contains("network", ignoreCase = true) == true ->
                "Erro de conexão. Verifique sua internet"

            else -> "Erro ao enviar email de recuperação. Tente novamente."
        }
    }

    /**
     * Resets the success state to allow sending another email
     */
    fun resetSuccessState() {
        _uiState.value = _uiState.value.copy(isEmailSent = false)
    }
}