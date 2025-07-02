package com.miga.piggy.home.data.repository

import com.miga.piggy.home.data.datasource.FinancialRemoteDataSource
import com.miga.piggy.balance.data.model.toDto
import com.miga.piggy.balance.domain.entity.Balance
import com.miga.piggy.home.data.model.toDto
import com.miga.piggy.home.domain.entity.Category
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.home.domain.repository.FinancialRepository
import com.miga.piggy.transaction.data.model.toDto

class FinancialRepositoryImpl(
    private val remoteDataSource: FinancialRemoteDataSource
) : FinancialRepository {

    override suspend fun getBalance(userId: String): Balance? {
        return remoteDataSource.getBalance(userId)?.toDomain()
    }

    override suspend fun updateBalance(balance: Balance): Result<Unit> {
        return remoteDataSource.updateBalance(balance.toDto())
    }

    override suspend fun getTransactions(userId: String): List<Transaction> {
        return remoteDataSource.getTransactions(userId).map { it.toDomain() }
    }

    override suspend fun getTransactionsByType(userId: String, type: TransactionType): List<Transaction> {
        return remoteDataSource.getTransactionsByType(userId, type.name).map { it.toDomain() }
    }

    override suspend fun addTransaction(transaction: Transaction): Result<String> {
        return remoteDataSource.addTransaction(transaction.toDto())
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        return remoteDataSource.updateTransaction(transaction.toDto())
    }

    override suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return remoteDataSource.deleteTransaction(transactionId)
    }

    override suspend fun getCategories(): List<Category> {
        return remoteDataSource.getCategories().map { it.toDomain() }
    }

    override suspend fun addCategory(category: Category): Result<String> {
        return remoteDataSource.addCategory(category.toDto())
    }
}