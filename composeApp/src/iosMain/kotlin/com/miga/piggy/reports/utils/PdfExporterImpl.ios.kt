package com.miga.piggy.reports.utils

import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.utils.formatters.formatDouble
import kotlinx.cinterop.*
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

actual class PdfExporterImpl : PdfExporter {

    @OptIn(ExperimentalForeignApi::class)
    actual override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult = withContext(Dispatchers.Default) {

        try {
            val documentsPath = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory,
                NSUserDomainMask,
                true
            ).first() as String

            val fileName = "relatorio_financeiro_${NSDate().timeIntervalSince1970.toLong()}.pdf"
            val filePath = "$documentsPath/$fileName"

            val pageRect = CGRectMake(0.0, 0.0, 612.0, 792.0)

            UIGraphicsBeginPDFContextToFile(filePath, pageRect, null)
            UIGraphicsBeginPDFPage()

            val context = UIGraphicsGetCurrentContext()

            CGContextTranslateCTM(context, 0.0, pageRect.size.toDouble())
            CGContextScaleCTM(context, 1.0, -1.0)

            var yPosition = 50.0

            drawText("Relatório Financeiro", 50.0, yPosition, 24.0)
            yPosition += 40.0

            val dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "dd/MM/yyyy"
            val currentDate = dateFormatter.stringFromDate(NSDate())
            drawText("Data: $currentDate", 400.0, yPosition, 12.0)
            yPosition += 40.0

            drawText("Resumo Financeiro", 50.0, yPosition, 18.0)
            yPosition += 40.0

            drawText("Receitas: R$ ${formatDouble(monthlyIncome)}", 50.0, yPosition, 14.0)
            yPosition += 25.0

            drawText("Gastos: R$ ${formatDouble(monthlyExpenses)}", 50.0, yPosition, 14.0)
            yPosition += 25.0

            val balance = monthlyIncome - monthlyExpenses
            drawText("Saldo: R$ ${formatDouble(balance)}", 50.0, yPosition, 14.0)
            yPosition += 50.0

            if (expensesByCategory.isNotEmpty()) {
                drawText("Gastos por Categoria", 50.0, yPosition, 18.0)
                yPosition += 30.0

                expensesByCategory.forEach { (category, amount) ->
                    drawText("$category: R$ ${formatDouble(amount)}", 70.0, yPosition, 12.0)
                    yPosition += 20.0
                }
                yPosition += 20.0
            }

            if (incomeByCategory.isNotEmpty()) {
                drawText("Receitas por Categoria", 50.0, yPosition, 18.0)
                yPosition += 30.0

                incomeByCategory.forEach { (category, amount) ->
                    drawText("$category: R$ ${formatDouble(amount)}", 70.0, yPosition, 12.0)
                    yPosition += 20.0
                }
                yPosition += 20.0
            }

            if (recentTransactions.isNotEmpty() && yPosition < 700.0) {
                drawText("Transações Recentes", 50.0, yPosition, 18.0)
                yPosition += 30.0

                recentTransactions.take(5).forEach { transaction ->
                    val transactionText = "${transaction.description} - ${transaction.category} - ${transaction.date} - R$ ${formatDouble(transaction.amount)}"
                    drawText(transactionText, 70.0, yPosition, 10.0)
                    yPosition += 18.0
                }
            }

            UIGraphicsEndPDFContext()

            PdfExportResult(
                success = true,
                filePath = filePath,
                error = null
            )

        } catch (e: Exception) {
            PdfExportResult(
                success = false,
                filePath = null,
                error = "Erro ao gerar PDF: ${e.message}"
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun drawText(text: String, x: Double, y: Double, fontSize: Double) {
        val font = UIFont.systemFontOfSize(fontSize)
        val attributedString = NSMutableAttributedString.create(string = text)

        attributedString.addAttribute(
            NSFontAttributeName,
            value = font,
            range = NSMakeRange(0u, text.length.toULong())
        )

        attributedString.addAttribute(
            NSForegroundColorAttributeName,
            value = UIColor.blackColor,
            range = NSMakeRange(0u, text.length.toULong())
        )

        attributedString.drawAtPoint(CGPointMake(x, y))
    }
}