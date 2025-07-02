package com.miga.piggy.balance.domain.usecases

import com.miga.piggy.balance.domain.entity.Balance
import com.miga.piggy.home.domain.repository.FinancialRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class UpdateBalanceUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(balance: Balance): Result<Unit> {
        return repository.updateBalance(balance.copy(updatedAt = Clock.System.now().toEpochMilliseconds()))
    }
}