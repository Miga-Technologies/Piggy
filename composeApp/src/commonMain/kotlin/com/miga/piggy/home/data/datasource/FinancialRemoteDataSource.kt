package com.miga.piggy.home.data.datasource

import com.miga.piggy.balance.data.model.BalanceDto
import com.miga.piggy.home.data.model.CategoryDto
import com.miga.piggy.transaction.data.model.TransactionDto
import dev.gitlive.firebase.firestore.FirebaseFirestore

interface FinancialRemoteDataSource {
    suspend fun getBalance(userId: String): BalanceDto?
    suspend fun updateBalance(balance: BalanceDto): Result<Unit>
    suspend fun getTransactions(userId: String): List<TransactionDto>
    suspend fun getTransactionsByType(userId: String, type: String): List<TransactionDto>
    suspend fun addTransaction(transaction: TransactionDto): Result<String>
    suspend fun updateTransaction(transaction: TransactionDto): Result<Unit>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
    suspend fun getCategories(): List<CategoryDto>
    suspend fun addCategory(category: CategoryDto): Result<String>
}

class FinancialRemoteDataSourceImpl(
    private val firestore: FirebaseFirestore
) : FinancialRemoteDataSource {

    companion object {
        private const val BALANCES_COLLECTION = "balances"
        private const val TRANSACTIONS_COLLECTION = "transactions"
        private const val CATEGORIES_COLLECTION = "categories"
    }

    override suspend fun getBalance(userId: String): BalanceDto? {
        return try {
            val snapshot = firestore.collection(BALANCES_COLLECTION)
                .where { "userId" equalTo userId }
                .get()

            snapshot.documents.firstOrNull()?.data<BalanceDto>()
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun updateBalance(balance: BalanceDto): Result<Unit> {
        return try {
            if (balance.id.isEmpty()) {
                // Criar novo saldo
                val docRef = firestore.collection(BALANCES_COLLECTION).document
                val newBalance = balance.copy(id = docRef.id)
                docRef.set(newBalance)
            } else {
                // Atualizar saldo existente
                firestore.collection(BALANCES_COLLECTION)
                    .document(balance.id)
                    .set(balance)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTransactions(userId: String): List<TransactionDto> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .where { "userId" equalTo userId }
                .get()

            snapshot.documents.map { it.data<TransactionDto>() }
                .sortedByDescending { it.date }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getTransactionsByType(userId: String, type: String): List<TransactionDto> {
        return try {
            val snapshot = firestore.collection(TRANSACTIONS_COLLECTION)
                .where { "userId" equalTo userId }
                .where { "type" equalTo type }
                .get()

            snapshot.documents.map { it.data<TransactionDto>() }
                .sortedByDescending { it.date }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun addTransaction(transaction: TransactionDto): Result<String> {
        return try {
            val docRef = firestore.collection(TRANSACTIONS_COLLECTION).document
            val newTransaction = transaction.copy(id = docRef.id)
            docRef.set(newTransaction)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTransaction(transaction: TransactionDto): Result<Unit> {
        return try {
            firestore.collection(TRANSACTIONS_COLLECTION)
                .document(transaction.id)
                .set(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTransaction(transactionId: String): Result<Unit> {
        return try {
            firestore.collection(TRANSACTIONS_COLLECTION)
                .document(transactionId)
                .delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCategories(): List<CategoryDto> {
        return try {
            val snapshot = firestore.collection(CATEGORIES_COLLECTION)
                .get()

            snapshot.documents.map { it.data<CategoryDto>() }
        } catch (_: Exception) {
            getDefaultCategories()
        }
    }

    override suspend fun addCategory(category: CategoryDto): Result<String> {
        return try {
            val docRef = firestore.collection(CATEGORIES_COLLECTION).document
            val newCategory = category.copy(id = docRef.id)
            docRef.set(newCategory)
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDefaultCategories(): List<CategoryDto> {
        return listOf(
            CategoryDto("1", "Alimentação", "EXPENSE", "#FF6200", true),
            CategoryDto("2", "Transporte", "EXPENSE", "#03DAC6", true),
            CategoryDto("3", "Lazer", "EXPENSE", "#6200EE", true),
            CategoryDto("4", "Saúde", "EXPENSE", "#4CAF50", true),
            CategoryDto("5", "Outros", "EXPENSE", "#FF5722", true),
            CategoryDto("6", "Salário", "INCOME", "#4CAF50", true),
            CategoryDto("7", "Freelance", "INCOME", "#03DAC6", true),
            CategoryDto("8", "Investimentos", "INCOME", "#6200EE", true)
        )
    }
}