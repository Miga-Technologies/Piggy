package com.miga.piggy.transaction.domain.usecases

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetTransactionsUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(userId: String): List<Transaction> {
        return repository.getTransactions(userId)
    }
}
