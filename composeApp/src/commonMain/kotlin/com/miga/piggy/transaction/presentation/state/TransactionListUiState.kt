package com.miga.piggy.transaction.presentation.state

import com.miga.piggy.transaction.domain.entity.Transaction

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)