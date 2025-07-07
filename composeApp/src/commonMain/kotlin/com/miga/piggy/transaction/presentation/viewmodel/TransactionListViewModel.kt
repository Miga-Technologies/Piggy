package com.miga.piggy.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.domain.usecases.GetTransactionsUseCase
import com.miga.piggy.transaction.presentation.state.TransactionListUiState
import com.miga.piggy.home.domain.repository.FinancialRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionListViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val repository: FinancialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    fun loadTransactions(userId: String, type: TransactionType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val transactions = repository.getTransactionsByType(userId, type)
                    .sortedByDescending { it.date }

                _uiState.value = _uiState.value.copy(
                    transactions = transactions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar transações: ${e.message}"
                )
            }
        }
    }

    fun loadAllTransactions(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val transactions = getTransactionsUseCase(userId)
                    .sortedByDescending { it.date }

                _uiState.value = _uiState.value.copy(
                    transactions = transactions,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar transações: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}