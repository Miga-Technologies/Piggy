package com.miga.piggy.balance.domain.entity

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class Balance(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds()
)