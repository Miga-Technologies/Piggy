package com.miga.piggy.balance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.balance.presentation.state.EditBalanceUiState
import com.miga.piggy.balance.domain.usecases.GetBalanceUseCase
import com.miga.piggy.balance.domain.usecases.UpdateBalanceUseCase
import com.miga.piggy.utils.formatters.formatDouble
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditBalanceViewModel(
    private val getBalanceUseCase: GetBalanceUseCase,
    private val updateBalanceUseCase: UpdateBalanceUseCase,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditBalanceUiState())
    val uiState: StateFlow<EditBalanceUiState> = _uiState.asStateFlow()

    init {
        loadCurrentBalance()
    }

    private fun loadCurrentBalance() {
        val userId = authViewModel.uiState.value.user?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val balance = getBalanceUseCase(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentBalance = balance.amount,
                    balanceText = formatDouble(balance.amount)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar saldo: ${e.message}"
                )
            }
        }
    }

    fun updateBalanceText(text: String) {
        // Permite apenas números e vírgula/ponto
        val filteredText = text.filter { it.isDigit() || it == '.' || it == ',' }
            .replace(',', '.') // Converte vírgula para ponto

        _uiState.value = _uiState.value.copy(balanceText = filteredText)
    }

    fun saveBalance() {
        val userId = authViewModel.uiState.value.user?.id ?: return
        val balanceText = _uiState.value.balanceText

        if (balanceText.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Digite um valor válido")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val amount = balanceText.toDoubleOrNull()
                if (amount == null || amount < 0) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Digite um valor válido (maior ou igual a zero)"
                    )
                    return@launch
                }

                val currentBalance = getBalanceUseCase(userId)
                val updatedBalance = currentBalance.copy(amount = amount)

                updateBalanceUseCase(updatedBalance).fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            success = true,
                            currentBalance = amount
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Erro ao salvar: ${error.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro inesperado: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false)
    }
}