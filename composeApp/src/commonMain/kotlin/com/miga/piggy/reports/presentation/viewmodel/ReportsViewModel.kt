package com.miga.piggy.reports.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miga.piggy.home.domain.repository.FinancialRepository
import com.miga.piggy.reports.presentation.state.ReportsUiState
import com.miga.piggy.reports.utils.PdfExporter
import com.miga.piggy.transaction.domain.entity.TransactionType
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

    fun loadReports(userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val now = Clock.System.now()
                val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
                val currentMonth = currentDateTime.month
                val currentYear = currentDateTime.year

                // Get all transactions for current month
                val allTransactions = repository.getTransactions(userId)
                val currentMonthTransactions = allTransactions.filter { transaction ->
                    val transactionDate = Instant.fromEpochMilliseconds(transaction.date)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                    transactionDate.month == currentMonth && transactionDate.year == currentYear
                }

                // Calculate monthly income and expenses
                val monthlyIncome = currentMonthTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val monthlyExpenses = currentMonthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                // Group by category
                val expensesByCategory = currentMonthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

                val incomeByCategory = currentMonthTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

                // Get recent transactions (last 10)
                val recentTransactions = allTransactions
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

    fun exportToPdf(userId: String) {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val success = pdfExporter.exportReportToPdf(
                    monthlyIncome = state.monthlyIncome,
                    monthlyExpenses = state.monthlyExpenses,
                    expensesByCategory = state.expensesByCategory,
                    incomeByCategory = state.incomeByCategory,
                    recentTransactions = state.recentTransactions
                )

                if (success) {
                    _uiState.value = _uiState.value.copy(pdfExported = true)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Erro ao exportar PDF")
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