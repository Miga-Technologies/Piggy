package com.miga.piggy.transaction.domain.usecases

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.home.domain.repository.FinancialRepository

class GetTransactionsByTypeUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(userId: String, type: TransactionType): List<Transaction> {
        return repository.getTransactionsByType(userId, type)
    }
}