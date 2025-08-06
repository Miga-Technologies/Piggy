package com.miga.piggy.transaction.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.domain.usecases.GetTransactionsUseCase
import com.miga.piggy.transaction.domain.usecases.DeleteTransactionUseCase
import com.miga.piggy.transaction.presentation.state.TransactionListUiState
import com.miga.piggy.home.domain.repository.FinancialRepository
import com.miga.piggy.utils.ui.MonthYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionListViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val repository: FinancialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    fun loadTransactions(userId: String, type: TransactionType, selectedMonth: MonthYear? = null) {
        viewModelScope.launch {
            val monthToUse = selectedMonth ?: _uiState.value.selectedMonth
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedMonth = monthToUse
            )

            try {
                val allTransactions = repository.getTransactionsByType(userId, type)

                // Filter by selected month
                val monthRange = monthToUse.getMonthRange()
                val filteredTransactions = allTransactions.filter { transaction ->
                    transaction.date >= monthRange.first && transaction.date <= monthRange.second
                }.sortedByDescending { it.date }

                _uiState.value = _uiState.value.copy(
                    transactions = filteredTransactions,
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

    fun loadAllTransactions(userId: String, selectedMonth: MonthYear? = null) {
        viewModelScope.launch {
            val monthToUse = selectedMonth ?: _uiState.value.selectedMonth
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedMonth = monthToUse
            )

            try {
                val allTransactions = getTransactionsUseCase(userId)

                // Filter by selected month
                val monthRange = monthToUse.getMonthRange()
                val filteredTransactions = allTransactions.filter { transaction ->
                    transaction.date >= monthRange.first && transaction.date <= monthRange.second
                }.sortedByDescending { it.date }

                _uiState.value = _uiState.value.copy(
                    transactions = filteredTransactions,
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

    /**
     * Changes the selected month and reloads transactions
     */
    fun changeSelectedMonth(userId: String, monthYear: MonthYear, type: TransactionType) {
        loadTransactions(userId, type, monthYear)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun deleteTransaction(
        userId: String,
        transactionId: String,
        refreshType: TransactionType? = null
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                deleteTransactionUseCase(transactionId)
                // Refresh the list after deletion
                if (refreshType != null) {
                    loadTransactions(userId, refreshType)
                } else {
                    loadAllTransactions(userId)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao excluir transação: ${e.message}"
                )
            }
        }
    }
}