package com.miga.piggy.home.domain.repository

import com.miga.piggy.balance.domain.entity.Balance
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType

interface FinancialRepository {
    suspend fun getBalance(userId: String): Balance?
    suspend fun updateBalance(balance: Balance): Result<Unit>

    suspend fun getTransactions(userId: String): List<Transaction>
    suspend fun getTransactionsByType(userId: String, type: TransactionType): List<Transaction>
    suspend fun addTransaction(transaction: Transaction): Result<String>
    suspend fun updateTransaction(transaction: Transaction): Result<Unit>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>

    suspend fun getCategories(): List<Category>
    suspend fun addCategory(category: Category): Result<String>
}