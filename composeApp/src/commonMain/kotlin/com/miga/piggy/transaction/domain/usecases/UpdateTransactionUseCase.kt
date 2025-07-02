package com.miga.piggy.transaction.domain.usecases

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.home.domain.repository.FinancialRepository

class UpdateTransactionUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return repository.updateTransaction(transaction)
    }
}