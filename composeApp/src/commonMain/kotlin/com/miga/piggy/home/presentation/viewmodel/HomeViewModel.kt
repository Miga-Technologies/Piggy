package com.miga.piggy.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.home.domain.usecases.financial.GetFinancialSummaryUseCase
import com.miga.piggy.home.presentation.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFinancialData()
    }

    fun loadFinancialData() {
        val userId = authViewModel.uiState.value.user?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val summary = getFinancialSummaryUseCase(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    balance = summary.balance,
                    totalIncome = summary.totalIncome,
                    totalExpenses = summary.totalExpenses,
                    monthlyIncome = summary.monthlyIncome,
                    monthlyExpenses = summary.monthlyExpenses,
                    expensesByCategory = summary.expensesByCategory
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar dados: ${e.message}"
                )
            }
        }
    }

    fun refresh() {
        loadFinancialData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}