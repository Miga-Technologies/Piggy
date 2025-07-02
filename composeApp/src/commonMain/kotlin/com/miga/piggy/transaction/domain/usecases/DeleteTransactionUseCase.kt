package com.miga.piggy.transaction.domain.usecases

import com.miga.piggy.home.domain.repository.FinancialRepository

class DeleteTransactionUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(transactionId: String): Result<Unit> {
        return repository.deleteTransaction(transactionId)
    }
}