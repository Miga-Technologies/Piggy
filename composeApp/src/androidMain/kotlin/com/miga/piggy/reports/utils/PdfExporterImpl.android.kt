package com.miga.piggy.reports.utils

import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.TextAlignment
import com.miga.piggy.transaction.domain.entity.Transaction
import com.miga.piggy.transaction.domain.entity.TransactionType
import com.miga.piggy.utils.formatters.formatDouble
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

actual class PdfExporterImpl : PdfExporter {

    actual override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult = withContext(Dispatchers.IO) {

        try {
            val fileName = "relatorio_financeiro_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            val pdfWriter = PdfWriter(FileOutputStream(file))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            document.add(
                Paragraph("Relatório Financeiro")
                    .setFontSize(24f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)
            )

            document.add(
                Paragraph("Data: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20f)
            )

            document.add(
                Paragraph("Resumo Financeiro")
                    .setFontSize(18f)
                    .setMarginBottom(10f)
            )

            val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(20f)

            summaryTable.addHeaderCell(
                Cell().add(Paragraph("Tipo").setFontSize(14f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )
            summaryTable.addHeaderCell(
                Cell().add(Paragraph("Valor").setFontSize(14f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            )

            summaryTable.addCell(Cell().add(Paragraph("Receitas")))
            summaryTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(monthlyIncome)}")))

            summaryTable.addCell(Cell().add(Paragraph("Gastos")))
            summaryTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(monthlyExpenses)}")))

            summaryTable.addCell(Cell().add(Paragraph("Saldo")))
            summaryTable.addCell(
                Cell().add(Paragraph("R$ ${formatDouble(monthlyIncome - monthlyExpenses)}"))
                    .setBackgroundColor(if (monthlyIncome - monthlyExpenses >= 0) ColorConstants.GREEN else ColorConstants.LIGHT_GRAY)
            )

            document.add(summaryTable)

            // Gastos por categoria
            if (expensesByCategory.isNotEmpty()) {
                document.add(
                    Paragraph("Gastos por Categoria")
                        .setFontSize(18f)
                        .setMarginBottom(10f)
                )

                val expenseTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f)))
                    .setWidth(UnitValue.createPercentValue(100f))
                    .setMarginBottom(20f)

                expenseTable.addHeaderCell(
                    Cell().add(Paragraph("Categoria").setFontSize(14f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                expenseTable.addHeaderCell(
                    Cell().add(Paragraph("Valor").setFontSize(14f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )

                expensesByCategory.forEach { (category, amount) ->
                    expenseTable.addCell(Cell().add(Paragraph(category)))
                    expenseTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(amount)}")))
                }

                document.add(expenseTable)
            }

            // Receitas por categoria
            if (incomeByCategory.isNotEmpty()) {
                document.add(
                    Paragraph("Receitas por Categoria")
                        .setFontSize(18f)
                        .setMarginBottom(10f)
                )

                val incomeTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f)))
                    .setWidth(UnitValue.createPercentValue(100f))
                    .setMarginBottom(20f)

                incomeTable.addHeaderCell(
                    Cell().add(Paragraph("Categoria").setFontSize(14f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                incomeTable.addHeaderCell(
                    Cell().add(Paragraph("Valor").setFontSize(14f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )

                incomeByCategory.forEach { (category, amount) ->
                    incomeTable.addCell(Cell().add(Paragraph(category)))
                    incomeTable.addCell(Cell().add(Paragraph("R$ ${formatDouble(amount)}")))
                }

                document.add(incomeTable)
            }

            if (recentTransactions.isNotEmpty()) {
                document.add(
                    Paragraph("Transações Recentes")
                        .setFontSize(18f)
                        .setMarginBottom(10f)
                )

                val transactionTable = Table(UnitValue.createPercentArray(floatArrayOf(2f, 1f, 1f, 1f)))
                    .setWidth(UnitValue.createPercentValue(100f))

                transactionTable.addHeaderCell(
                    Cell().add(Paragraph("Descrição").setFontSize(12f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                transactionTable.addHeaderCell(
                    Cell().add(Paragraph("Categoria").setFontSize(12f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                transactionTable.addHeaderCell(
                    Cell().add(Paragraph("Data").setFontSize(12f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )
                transactionTable.addHeaderCell(
                    Cell().add(Paragraph("Valor").setFontSize(12f))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                )

                recentTransactions.take(10).forEach { transaction ->
                    transactionTable.addCell(Cell().add(Paragraph(transaction.description).setFontSize(10f)))
                    transactionTable.addCell(Cell().add(Paragraph(transaction.category).setFontSize(10f)))
                    transactionTable.addCell(Cell().add(Paragraph(transaction.date.toString()).setFontSize(10f)))

                    val valueCell = Cell().add(
                        Paragraph("R$ ${formatDouble(transaction.amount)}")
                            .setFontSize(10f)
                    )

                    if (transaction.type == TransactionType.INCOME) {
                        valueCell.setBackgroundColor(ColorConstants.GREEN)
                    } else {
                        valueCell.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    }

                    transactionTable.addCell(valueCell)
                }

                document.add(transactionTable)
            }

            document.close()

            PdfExportResult(
                success = true,
                filePath = file.absolutePath,
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
}