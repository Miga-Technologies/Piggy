package com.miga.piggy.home.domain.usecases.financial

import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.balance.domain.usecases.GetBalanceUseCase
import com.miga.piggy.transaction.domain.usecases.GetTransactionsUseCase
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class FinancialSummary(
    val balance: Double,
    val totalIncome: Double,
    val totalExpenses: Double,
    val monthlyIncome: Double,
    val monthlyExpenses: Double,
    val expensesByCategory: Map<String, Double>,
    val recentTransactions: List<Transaction>
)

class GetFinancialSummaryUseCase(
    private val getBalanceUseCase: GetBalanceUseCase,
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    suspend operator fun invoke(userId: String): FinancialSummary {
        val balance = getBalanceUseCase(userId)
        val transactions = getTransactionsUseCase(userId)

        val currentMonth = getCurrentMonthRange()
        val monthlyTransactions = transactions.filter {
            it.date >= currentMonth.first && it.date <= currentMonth.second
        }

        val income = transactions.filter { it.type == TransactionType.INCOME }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        val monthlyIncome = monthlyTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val monthlyExpenses = monthlyTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val expensesByCategory = monthlyTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

        val recentTransactions = transactions.sortedByDescending { it.date }.take(5)

        return FinancialSummary(
            balance = balance.amount,
            totalIncome = income.sumOf { it.amount },
            totalExpenses = expenses.sumOf { it.amount },
            monthlyIncome = monthlyIncome,
            monthlyExpenses = monthlyExpenses,
            expensesByCategory = expensesByCategory,
            recentTransactions = recentTransactions
        )
    }


    @OptIn(ExperimentalTime::class)
    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone).date

        val startOfMonth = LocalDate(today.year, today.month, 1)
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        val endOfMonth = startOfMonth
            .let {
                val nextMonth = LocalDate(today.year, today.month, 1)
                    .plus(1, DateTimeUnit.MONTH)
                nextMonth.atStartOfDayIn(timeZone)
                    .minus(1, DateTimeUnit.MILLISECOND, timeZone)
                    .toEpochMilliseconds()
            }

        return startOfMonth to endOfMonth
    }
}