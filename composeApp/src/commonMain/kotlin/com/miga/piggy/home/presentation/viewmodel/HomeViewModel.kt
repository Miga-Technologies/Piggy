package com.miga.piggy.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.home.domain.usecases.financial.GetFinancialSummaryUseCase
import com.miga.piggy.home.presentation.state.HomeUiState
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.domain.usecases.AddTransactionUseCase
import com.miga.piggy.utils.ui.MonthYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class HomeViewModel(
    private val getFinancialSummaryUseCase: GetFinancialSummaryUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFinancialData()
    }

    fun loadFinancialData() {
        val userId = authViewModel.uiState.value.user?.id ?: return
        val selectedMonth = _uiState.value.selectedMonth

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val summary = getFinancialSummaryUseCase(userId, selectedMonth)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    balance = summary.balance,
                    totalIncome = summary.totalIncome,
                    totalExpenses = summary.totalExpenses,
                    monthlyIncome = summary.monthlyIncome,
                    monthlyExpenses = summary.monthlyExpenses,
                    expensesByCategory = summary.expensesByCategory,
                    recentTransactions = summary.recentTransactions
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar dados: ${e.message}"
                )
            }
        }
    }

    /**
     * Changes the selected month and reloads financial data
     */
    fun changeSelectedMonth(monthYear: MonthYear) {
        _uiState.value = _uiState.value.copy(selectedMonth = monthYear)
        loadFinancialData()
    }

    /**
     * Adiciona um gasto rápido para categorias predefinidas
     */
    fun addQuickExpense(amount: Double, categoryName: String) {
        val userId = authViewModel.uiState.value.user?.id ?: return

        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = "",
                    userId = userId,
                    amount = amount,
                    description = "Pagamento de $categoryName",
                    category = categoryName,
                    type = TransactionType.EXPENSE,
                    date = Clock.System.now().toEpochMilliseconds()
                )

                addTransactionUseCase(transaction).fold(
                    onSuccess = {
                        loadFinancialData() // Recarrega os dados após adicionar com sucesso
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            error = "Erro ao adicionar gasto: ${error.message}"
                        )
                    }
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao adicionar gasto: ${e.message}"
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