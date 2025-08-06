package com.miga.piggy.transaction.presentation.state

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.utils.ui.MonthYear

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val selectedMonth: MonthYear = MonthYear.current(),
    val isLoading: Boolean = false,
    val error: String? = null
)