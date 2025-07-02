package com.miga.piggy.balance.data.model

import com.miga.piggy.balance.domain.entity.Balance
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
@OptIn(ExperimentalTime::class)
data class BalanceDto(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
) {
    fun toDomain(): Balance = Balance(
        id = id,
        userId = userId,
        amount = amount,
        updatedAt = updatedAt
    )
}

fun Balance.toDto(): BalanceDto = BalanceDto(
    id = id,
    userId = userId,
    amount = amount,
    updatedAt = updatedAt
)
