package com.miga.piggy.transaction.data.model

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@OptIn(ExperimentalTime::class)
data class TransactionDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "EXPENSE",
    val amount: Double = 0.0,
    val category: String = "",
    val description: String = "",
    val date: Long = Clock.System.now().toEpochMilliseconds(),
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        userId = userId,
        type = TransactionType.valueOf(type),
        amount = amount,
        category = category,
        description = description,
        date = date,
        createdAt = createdAt
    )
}

fun Transaction.toDto(): TransactionDto = TransactionDto(
    id = id,
    userId = userId,
    type = type.name,
    amount = amount,
    category = category,
    description = description,
    date = date,
    createdAt = createdAt
)
