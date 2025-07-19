package com.miga.piggy.transaction.presentation.state

import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.TransactionType
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class AddTransactionUiState(
    val isLoading: Boolean = false,
    val transactionType: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val description: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val selectedDate: Long = Clock.System.now().toEpochMilliseconds(),
    val isCategoryDropdownExpanded: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)