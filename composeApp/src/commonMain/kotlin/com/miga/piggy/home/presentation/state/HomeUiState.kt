package com.miga.piggy.home.presentation.state

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.utils.ui.MonthYear

data class HomeUiState(
    val isLoading: Boolean = false,
    val selectedMonth: MonthYear = MonthYear.current(),
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val expensesByCategory: Map<String, Double> = emptyMap(),
    val recentTransactions: List<Transaction> = emptyList(),
    val error: String? = null
)