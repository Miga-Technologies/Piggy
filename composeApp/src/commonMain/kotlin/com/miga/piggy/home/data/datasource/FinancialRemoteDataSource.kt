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
    suspend fun updateCategory(category: CategoryDto): Result<Unit>
    suspend fun deleteCategory(categoryId: String): Result<Unit>
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
            val firestoreCategories = firestore.collection(CATEGORIES_COLLECTION)
                .get()
                .documents
                .map { it.data<CategoryDto>() }
                .filter { !it.isDefault } // Filtrar apenas categorias personalizadas do Firestore

            // Sempre retornar categorias padrão + categorias personalizadas
            getDefaultCategories() + firestoreCategories
        } catch (_: Exception) {
            // Em caso de erro, retornar apenas as categorias padrão
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

    override suspend fun updateCategory(category: CategoryDto): Result<Unit> {
        return try {
            firestore.collection(CATEGORIES_COLLECTION)
                .document(category.id)
                .set(category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            firestore.collection(CATEGORIES_COLLECTION)
                .document(categoryId)
                .delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getDefaultCategories(): List<CategoryDto> {
        return listOf(
            // Categorias essenciais com cores dos gradientes
            CategoryDto("agua", "Água", "EXPENSE", "#29B6F6", true),
            CategoryDto("energia", "Energia", "EXPENSE", "#FF9800", true),
            CategoryDto("internet", "Internet", "EXPENSE", "#4CAF50", true),
            CategoryDto("telefone", "Telefone", "EXPENSE", "#1E88E5", true),

            // Outras categorias comuns
            CategoryDto("alimentacao", "Alimentação", "EXPENSE", "#FF6200", true),
            CategoryDto("transporte", "Transporte", "EXPENSE", "#03DAC6", true),
            CategoryDto("lazer", "Lazer", "EXPENSE", "#9C27B0", true),
            CategoryDto("saude", "Saúde", "EXPENSE", "#4CAF50", true),
            CategoryDto("educacao", "Educação", "EXPENSE", "#2196F3", true),
            CategoryDto("casa", "Casa", "EXPENSE", "#FF5722", true),
            CategoryDto("outros_gastos", "Outros Gastos", "EXPENSE", "#607D8B", true),

            // Categorias de receita
            CategoryDto("salario", "Salário", "INCOME", "#4CAF50", true),
            CategoryDto("investimentos", "Investimentos", "INCOME", "#9C27B0", true),
            CategoryDto("outros_receitas", "Outras Receitas", "INCOME", "#FF9800", true)
        )
    }
}