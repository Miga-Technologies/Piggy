package com.miga.piggy.reports.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
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

actual class PdfExporterImpl(
    private val context: Context
) : PdfExporter {

    actual override suspend fun exportReportToPdf(
        monthlyIncome: Double,
        monthlyExpenses: Double,
        expensesByCategory: Map<String, Double>,
        incomeByCategory: Map<String, Double>,
        recentTransactions: List<Transaction>
    ): PdfExportResult = withContext(Dispatchers.IO) {

        try {
            val fileName = "relatorio_financeiro_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"

            // Usar o diretório de cache da aplicação para evitar problemas de permissão
            val cacheDir = File(context.cacheDir, "reports")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val file = File(cacheDir, fileName)

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

            // Compartilhar o PDF após a criação
            sharePdf(file)

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

    private fun sharePdf(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Relatório Financeiro")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Compartilhar relatório")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(chooserIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}