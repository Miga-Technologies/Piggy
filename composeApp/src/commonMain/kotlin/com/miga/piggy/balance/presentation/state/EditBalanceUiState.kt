package com.miga.piggy.balance.presentation.state

data class EditBalanceUiState(
    val isLoading: Boolean = false,
    val currentBalance: Double = 0.0,
    val balanceText: String = "",
    val error: String? = null,
    val success: Boolean = false
)