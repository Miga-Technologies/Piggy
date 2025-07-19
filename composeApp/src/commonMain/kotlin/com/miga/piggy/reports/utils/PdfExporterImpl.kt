package com.miga.piggy.reports.utils

import com.miga.piggy.transaction.domain.entity.Transaction

interface PdfExporter {
    suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult
}

data class PdfExportResult(
    val success: Boolean,
    val filePath: String? = null,
    val error: String? = null
)

expect class PdfExporterImpl : PdfExporter {
    override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult
}