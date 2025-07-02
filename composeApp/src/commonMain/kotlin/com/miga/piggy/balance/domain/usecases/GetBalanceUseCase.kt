package com.miga.piggy.balance.domain.usecases

import com.miga.piggy.balance.domain.entity.Balance
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetBalanceUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(userId: String): Balance {
        return repository.getBalance(userId) ?: Balance(userId = userId)
    }
}