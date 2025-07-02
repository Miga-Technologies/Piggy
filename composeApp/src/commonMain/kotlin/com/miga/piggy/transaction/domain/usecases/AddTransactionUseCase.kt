package com.miga.piggy.transaction.domain.usecases

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.home.domain.repository.FinancialRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AddTransactionUseCase(
    private val repository: FinancialRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<String> {
        val now = Clock.System.now().toEpochMilliseconds()
        val newTransaction = transaction.copy(
            date = if (transaction.date == 0L) now else transaction.date,
            createdAt = now
        )
        return repository.addTransaction(newTransaction)
    }
}