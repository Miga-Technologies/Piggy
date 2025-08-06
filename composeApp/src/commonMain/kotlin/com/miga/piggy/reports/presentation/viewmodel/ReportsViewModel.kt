package com.miga.piggy.reports.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.home.domain.repository.FinancialRepository
import com.miga.piggy.reports.presentation.state.ReportsUiState
import com.miga.piggy.reports.utils.PdfExporter
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.utils.ui.MonthYear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalTime::class)
class ReportsViewModel(
    private val repository: FinancialRepository,
    private val pdfExporter: PdfExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    fun loadReports(userId: String, selectedMonth: MonthYear = MonthYear.current()) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                selectedMonth = selectedMonth
            )

            try {
                // Get all transactions
                val allTransactions = repository.getTransactions(userId)

                // Filter transactions for selected month
                val monthRange = selectedMonth.getMonthRange()
                val monthlyTransactions = allTransactions.filter { transaction ->
                    transaction.date >= monthRange.first && transaction.date <= monthRange.second
                }

                // Calculate monthly income and expenses
                val monthlyIncome = monthlyTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val monthlyExpenses = monthlyTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                // Group by category
                val expensesByCategory = monthlyTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

                val incomeByCategory = monthlyTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

                // Get recent transactions from selected month
                val recentTransactions = monthlyTransactions
                    .sortedByDescending { it.date }
                    .take(10)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    monthlyIncome = monthlyIncome,
                    monthlyExpenses = monthlyExpenses,
                    monthlyBalance = monthlyIncome - monthlyExpenses,
                    expensesByCategory = expensesByCategory,
                    incomeByCategory = incomeByCategory,
                    recentTransactions = recentTransactions
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro ao carregar relatórios: ${e.message}"
                )
            }
        }
    }

    /**
     * Changes the selected month and reloads reports
     */
    fun changeSelectedMonth(userId: String, monthYear: MonthYear) {
        loadReports(userId, monthYear)
    }

    fun exportToPdf() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val result = pdfExporter.exportReportToPdf(
                    monthlyIncome = state.monthlyIncome,
                    monthlyExpenses = state.monthlyExpenses,
                    expensesByCategory = state.expensesByCategory,
                    incomeByCategory = state.incomeByCategory,
                    recentTransactions = state.recentTransactions
                )

                if (result.success) {
                    _uiState.value = _uiState.value.copy(
                        pdfExported = true,
                        pdfPath = result.filePath
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        error = result.error ?: "Erro desconhecido ao exportar PDF"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Erro ao exportar PDF: ${e.message}"
                )
            }
        }
    }

    fun clearPdfExported() {
        _uiState.value = _uiState.value.copy(pdfExported = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}