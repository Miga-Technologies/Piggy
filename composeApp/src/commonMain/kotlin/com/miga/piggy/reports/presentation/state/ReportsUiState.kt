package com.miga.piggy.reports.presentation.state

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.utils.ui.MonthYear

data class ReportsUiState(
    val isLoading: Boolean = false,
    val selectedMonth: MonthYear = MonthYear.current(),
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val incomeByCategory: Map<String, Double> = emptyMap(),
    val recentTransactions: List<Transaction> = emptyList(),
    val pdfExported: Boolean = false,
    val pdfPath: String? = null,
    val error: String? = null
)