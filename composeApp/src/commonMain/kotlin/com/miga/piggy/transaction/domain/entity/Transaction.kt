package com.miga.piggy.transaction.domain.entity

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)

enum class TransactionType {
    EXPENSE,
    INCOME
}